package com.cryptopaths.cryptofm.filemanager.utils;

import android.os.Environment;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/13/17.
 * holds shared variables
 */

public class SharedData {
    public static boolean           KEYS_GENERATED = false;
    public static boolean           SELECTION_MODE = false;
    public static boolean           STARTED_IN_SELECTION_MODE=false;
    public static boolean           IS_IN_COPY_MODE = false;
    public static boolean           IS_TASK_CANCELED=false;
    public static boolean           ALREADY_INSTANTIATED=false;
    public static boolean           IS_COPYING_NOT_MOVING=false;
    public static ArrayList<String> CURRENT_RUNNING_OPERATIONS=new ArrayList<>();
    public static final String      FILES_ROOT_DIRECTORY= Environment.getExternalStorageDirectory().getPath()+"/";

    public static int               SELECT_COUNT=0;
    public static String            USERNAME;
    public static String            DB_PASSWWORD;
    public static String            KEY_PASSWORD;

    private static SharedData   mSharedData;
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
