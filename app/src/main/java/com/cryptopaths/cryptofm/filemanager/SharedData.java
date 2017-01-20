package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.os.Environment;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/13/17.
 */

public class SharedData {
    public static  boolean NO_FILES_VIEW = true;
    public static boolean SELECTION_MODE = true;
    public static Boolean IS_IN_COPY_MODE = false;
    public static ArrayList<String> CURRENT_RUNNING_OPERATIONS=new ArrayList<>();
    public static final String    FILES_ROOT_DIRECTORY= Environment.getExternalStorageDirectory().getPath()+"/";
    public static boolean   IS_TASK_CANCELED=false;

    public static int SELECT_COUNT=0;
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

    /**
     *
     * @param path to check if any operation is going on it
     * @return true if no operation is going on else false
     */
    public static boolean checkIfInRunningTask(String path){
        for (String p: CURRENT_RUNNING_OPERATIONS) {
            if(path.contains(p) || p.contains(path)){
                return true;
            }
        }
        return false;
    }

}
