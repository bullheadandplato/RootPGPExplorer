/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.filemanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.extras.BlurHelper;
import com.slownet5.pgprootexplorer.filemanager.listview.FileFillerWrapper;
import com.slownet5.pgprootexplorer.filemanager.listview.FileListAdapter;
import com.slownet5.pgprootexplorer.filemanager.listview.FileSelectionManagement;
import com.slownet5.pgprootexplorer.filemanager.listview.RecyclerViewSwipeHandler;
import com.slownet5.pgprootexplorer.filemanager.utils.ActionViewHandler;
import com.slownet5.pgprootexplorer.filemanager.utils.FragmentCallbacks;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.filemanager.utils.TaskHandler;
import com.slownet5.pgprootexplorer.filemanager.utils.TaskHandlerWrapper;


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
    private TaskHandlerWrapper      mTaskHandler;
    private ActionViewHandler       mActionViewHandler;
    private int                     mFragmentPosition;
    private boolean                 mIsEmptyFolder=false;
    private String                  mRootPath;
    private String mPath;
    private ViewGroup viewGroup;
    private FragmentCallbacks mCallbacks;
    private  View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: view only created once");
        if(SharedData.ALREADY_INSTANTIATED){
            Log.d(TAG, "onCreateView: yes saved instance is not null");
        }
        view= inflater.inflate(R.layout.tabs_fragment_one,container,false);
        viewGroup=container;
        mRecyclerView=(RecyclerView) view.findViewById(R.id.fragment_recycler_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: view created");
        super.onViewCreated(view, savedInstanceState);
        this.view=view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: fragment activity created");
        super.onActivityCreated(savedInstanceState);
        blurView=view.findViewById(R.id.blur_layout);
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
        Log.d(TAG, "newInstance: path is: "+path);
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
        this.mRootPath=mPath;
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
    public String getRootPath() {
        return mRootPath;
    }

    public void init(){
        mLinearLayoutManager=new LinearLayoutManager(mContext);
        mGridLayoutManager=new GridLayoutManager(mContext,2);

        mFileFiller=new FileFillerWrapper();
        mFileAdapter= new FileListAdapter(mContext);
        mFileAdapter.setmFileFiller(mFileFiller);
        mManager=mFileAdapter.getmManager();
        mTaskHandler=new TaskHandlerWrapper(mContext,mFileAdapter);
        mFileAdapter.setmTaskHandler(mTaskHandler);
        mActionViewHandler=new ActionViewHandler(mContext,mManager,mTaskHandler);

        mHelper=new ItemTouchHelper(new RecyclerViewSwipeHandler(mContext,mTaskHandler,mFileAdapter));

        boolean whichLayout=getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("layout",true);
        if(whichLayout){
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mHelper.attachToRecyclerView(mRecyclerView);
        }else{
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        }
        mRecyclerView.setAdapter(mFileAdapter);
        mFileFiller.fillData(mPath,mFileAdapter);

    }
    void changeDirectory(String path,int mode) {

            mCurrentPath=path;
            Log.d("filesc", "current path: " + path);
            mFileFiller.fillData(path,mFileAdapter);
        if(mFileFiller.getTotalFilesCount()<1){
            return;
        }
        forwardAnimation(mode);

    }

    public void setmCurrentPath(String mCurrentPath) {
        this.mCurrentPath = mCurrentPath;
    }

    public ActionViewHandler getmActionViewHandler() {
        return mActionViewHandler;
    }

    public void toggleLayout() {
        if(SharedData.LINEAR_LAYOUTMANAGER) {
            Log.d(TAG, "toggleLayout: setting linear layout manager");
            if (mLinearLayoutManager == null) {
                mLinearLayoutManager = new LinearLayoutManager(mContext);
            }
            mHelper.attachToRecyclerView(mRecyclerView);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            reloadDataSet();
            new SharedPreferencesTask().execute(true);
        }else{
            Log.d(TAG, "toggleLayout: setting grid layout manager");
                if(mGridLayoutManager==null){
                    mGridLayoutManager=new GridLayoutManager(mContext,2);
                }
                mHelper.attachToRecyclerView(null);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
                reloadDataSet();
                new SharedPreferencesTask().execute(false);
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
        mTaskHandler.moveFiles(mCurrentPath,mFileAdapter);
    }

    public void reloadDataSet(){
       mRecyclerView.setAdapter(null);
        mRecyclerView.setAdapter(mFileAdapter);
        mFileAdapter.notifyDataSetChanged();
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }



    private class SharedPreferencesTask extends AsyncTask<Boolean,Void,Void>{

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

    public TaskHandlerWrapper getmTaskHandler(){
        return this.mTaskHandler;
    }

    private void forwardAnimation(final int mode){
        Animation animation= AnimationUtils.loadAnimation(mContext,R.anim.enter_from_left);
        if(mode==0){
            animation=AnimationUtils.loadAnimation(mContext,R.anim.enter_from_right);
        }

        mRecyclerView.setAnimation(animation);
        mRecyclerView.animate();
    }
    private boolean isBlur=false;
    View blurView;

    public void toggleBlur()
    {

        if(!isBlur){
            blur();
        }
        else{
            blurView.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            isBlur=false;
        }


    }
    private void blur(){
        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap bm = view.getDrawingCache();
        blurView.setBackground(new BitmapDrawable(getResources(),blur(getContext(),bm)));
        mRecyclerView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.VISIBLE);
        isBlur=true;
    }

    public boolean isBlur() {
        return isBlur;
    }

    public void setBlur(boolean blur) {
        isBlur = blur;
    }

    static final float BITMAP_SCALE = 0.4f;
        static final float BLUR_RADIUS = 7.5f;

        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
            }else{
                outputBitmap= BlurHelper.doBlur(image,(int)BLUR_RADIUS,false);
            }


            return outputBitmap;

    }

}
