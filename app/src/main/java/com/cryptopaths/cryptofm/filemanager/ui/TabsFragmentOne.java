package com.cryptopaths.cryptofm.filemanager.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.FragmentCallbacks;
import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptofm.filemanager.listview.FileFillerWrapper;
import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.listview.FileSelectionManagement;
import com.cryptopaths.cryptofm.filemanager.listview.RecyclerViewSwipeHandler;

/**
 * Created by Shadow on 1/21/2017.
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
    public static TabsFragmentOne newInstance(String path){
        Bundle bundle=new Bundle();
        bundle.putString(KEY,path);
        TabsFragmentOne tabsFragmentOne=new TabsFragmentOne();
        tabsFragmentOne.setArguments(bundle);
        return tabsFragmentOne;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: creating new instance");
        super.onCreate(savedInstanceState);
        mPath= getArguments().getString(KEY);
        if(SharedData.ALREADY_INSTANTIATED){
            Log.d(TAG, "onCreate: already created");
            init();
        }
    }
    public void init(){
        mLinearLayoutManager=new LinearLayoutManager(mContext);
        mGridLayoutManager=new GridLayoutManager(mContext,2);
        mFileAdapter= SharedData.getInstance().getFileListAdapter(mContext);
        mManager=SharedData.getInstance().getmFileSelectionManagement(mContext);
        mCurrentPath= Environment.getExternalStorageDirectory().getPath()+"/";
        mHelper=new ItemTouchHelper(new RecyclerViewSwipeHandler(mContext));

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        FileFillerWrapper.fillData(mPath,mContext);
        mRecyclerView.setAdapter(mFileAdapter);

    }

}
