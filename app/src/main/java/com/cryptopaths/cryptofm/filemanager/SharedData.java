package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;

/**
 * Created by tripleheader on 1/13/17.
 */

public class SharedData {
    public static Boolean IS_IN_COPY_MODE = false;
    public static String USERNAME;
    public static String DB_PASSWWORD;
    public static String KEY_PASSWORD;

    private static SharedData   mSharedData;
    private FileListAdapter     mFileListAdapter;
    private TaskHandler         mTaskHandler;
    /**
     * prevent public instance creation
     * Only can get instance via getInstance() method
     */
    private SharedData(){}

    /**
     *
     * @return the instance of this class
     */
    public static SharedData getInstance(){
        if(mSharedData==null){
            mSharedData=new SharedData();
        }
        return mSharedData;
    }

    public FileListAdapter getFileListAdapter(Context context){
        if(mFileListAdapter==null){
            mFileListAdapter=new FileListAdapter(context);
        }
        return mFileListAdapter;
    }
    FileListAdapter getFileListAdapter(){
        assert mFileListAdapter!=null;
        return mFileListAdapter;
    }

    TaskHandler getTaskHandler(Context context){
        if(mTaskHandler==null){
            mTaskHandler=new TaskHandler(context);
        }
        return mTaskHandler;
    }
    TaskHandler getTaskHandler(){
        assert mTaskHandler!=null;
        return mTaskHandler;
    }
}
