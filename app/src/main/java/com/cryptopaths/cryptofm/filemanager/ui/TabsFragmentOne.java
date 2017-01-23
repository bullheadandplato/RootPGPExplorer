package com.cryptopaths.cryptofm.filemanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.utils.ActionViewHandler;
import com.cryptopaths.cryptofm.filemanager.utils.FragmentCallbacks;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.utils.TaskHandler;
import com.cryptopaths.cryptofm.filemanager.listview.FileFillerWrapper;
import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.listview.FileSelectionManagement;
import com.cryptopaths.cryptofm.filemanager.listview.RecyclerViewSwipeHandler;

/**
 * Created by Shadow on 1/21/2017.
 *
 */

public class TabsFragmentOne extends Fragment {

    private static final String TAG=TabsFragmentOne.class.getName();
    private static final String KEY="path";
    private FileListAdapter         mFileAdapter;
    private RecyclerView            mRecyclerView;
    private LinearLayoutManager     mLinearLayoutManager;
    private GridLayoutManager       mGridLayoutManager;
    private ItemTouchHelper         mHelper;
    private FileSelectionManagement mManager;
    private String                  mCurrentPath;
    private Context                 mContext;
    private FileFillerWrapper       mFileFiller;
    private TaskHandler             mTaskHandler;
    private ActionViewHandler       mActionViewHandler;
    private int                     mFragmentPosition;
    private boolean                 mIsEmptyFolder=false;
    private String mPath;
    private FragmentCallbacks mCallbacks;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: view only created once");
        if(SharedData.ALREADY_INSTANTIATED){
            Log.d(TAG, "onCreateView: yes saved instance is not null");
        }
        View view= inflater.inflate(R.layout.tabs_fragment_one,container,false);
        mRecyclerView=(RecyclerView) view.findViewById(R.id.fragment_recycler_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: view created");
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: fragment activity created");
        super.onActivityCreated(savedInstanceState);
        mCallbacks=(FragmentCallbacks)getActivity();
        mContext=getActivity();
        mCallbacks.setCurrentFragment(this,mFragmentPosition);
        init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("saving","google");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mCallbacks.finishActionMode();
        super.onPause();
    }
    public static TabsFragmentOne newInstance(String path,int position){
        Bundle bundle=new Bundle();
        bundle.putString(KEY,path);
        bundle.putInt("pos",position);
        TabsFragmentOne tabsFragmentOne=new TabsFragmentOne();
        tabsFragmentOne.setArguments(bundle);
        return tabsFragmentOne;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: creating new instance");
        super.onCreate(savedInstanceState);
        mPath= getArguments().getString(KEY);
        mCurrentPath=mPath;
        mFragmentPosition=getArguments().getInt("pos");
        if(SharedData.ALREADY_INSTANTIATED){
            Log.d(TAG, "onCreate: already created");
            init();
        }
    }

    public ItemTouchHelper getmHelper() {
        return mHelper;
    }

    public String getmCurrentPath() {
        return mCurrentPath;
    }


    public void init(){
        mLinearLayoutManager=new LinearLayoutManager(mContext);
        mGridLayoutManager=new GridLayoutManager(mContext,2);

        mFileFiller=new FileFillerWrapper();;
        mFileAdapter= new FileListAdapter(mContext);
        mFileAdapter.setmFileFiller(mFileFiller);
        mManager=mFileAdapter.getmManager();
        mTaskHandler=new TaskHandler(mContext,mFileAdapter,mManager);
        mActionViewHandler=new ActionViewHandler(mContext,mManager,mTaskHandler);

        mHelper=new ItemTouchHelper(new RecyclerViewSwipeHandler(mContext,mTaskHandler,mFileAdapter));

        boolean whichLayout=getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("layout",true);
        if(whichLayout){
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mHelper.attachToRecyclerView(mRecyclerView);
        }else{
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        }
        mFileFiller.fillData(mPath,mContext);
        mRecyclerView.setAdapter(mFileAdapter);

    }
    void changeDirectory(String path) {
        this.mCurrentPath=path;
        Log.d("filesc", "current path: " + path);
        mFileFiller.fillData(path, mContext);
        if (mFileFiller.getTotalFilesCount() < 1) {
            mCallbacks.tellNoFiles();
            this.mIsEmptyFolder=true;
            return;
        }
            mFileAdapter.notifyDataSetChanged();
        }

    public void setmCurrentPath(String mCurrentPath) {
        this.mCurrentPath = mCurrentPath;
    }

    public ActionViewHandler getmActionViewHandler() {
        return mActionViewHandler;
    }

    public void toggleLayout(MenuItem item) {
        Log.d(TAG, "toggleLayout: Changing layout");
        if(item.getItemId()==R.id.items_view_menu_item){
            if(mRecyclerView.getLayoutManager()==mGridLayoutManager){
                item.setIcon(mContext.getDrawable(R.drawable.ic_grid_view));
                if(mLinearLayoutManager==null){
                    mLinearLayoutManager=new LinearLayoutManager(mContext);
                }
                mHelper.attachToRecyclerView(mRecyclerView);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
                new SharedPreferencesTask().execute(true);
            }else{
                item.setIcon(mContext.getDrawable(R.drawable.ic_items_view));
                if(mGridLayoutManager==null){
                    mGridLayoutManager=new GridLayoutManager(mContext,2);
                }
                mHelper.attachToRecyclerView(null);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
                new SharedPreferencesTask().execute(false);
            }
            mRecyclerView.requestLayout();
        }
    }

    public FileListAdapter getmFileAdapter() {
        return mFileAdapter;
    }

    public boolean ismIsEmptyFolder() {
        return mIsEmptyFolder;
    }

    public void setmIsEmptyFolder(boolean mIsEmptyFolder) {
        this.mIsEmptyFolder = mIsEmptyFolder;
    }

    public void executeCopyTask() {
        mTaskHandler.moveFiles(mCurrentPath);
    }

    class SharedPreferencesTask extends AsyncTask<Boolean,Void,Void>{

        @Override
        protected Void doInBackground(Boolean... booleen) {
            SharedPreferences prefs=getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=prefs.edit();
            editor.putBoolean("layout",booleen[0]);
            editor.apply();
            editor.commit();
            return null;
        }
    }
    public String getRootPath(){
        return mPath;
    }
}
