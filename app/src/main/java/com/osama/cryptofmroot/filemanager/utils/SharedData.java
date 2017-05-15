/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofmroot.filemanager.utils;

import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

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
    public volatile static ArrayList<String> CURRENT_RUNNING_OPERATIONS=new ArrayList<>();
    public static final String      FILES_ROOT_DIRECTORY= Environment.getExternalStorageDirectory().getPath()+"/";
    public static int               SELECT_COUNT=0;
    public static String            USERNAME;
    public static String            DB_PASSWORD;
    public static String            KEY_PASSWORD;
    public static boolean           DO_NOT_RESET_ICON=false;
    public static HashMap<String,String> symbolicLinks =new HashMap<>();
    public static boolean           GRID_LAYOUTMANAGER=false;
    public static boolean           IS_OPENWITH_SHOWN=false;
    /**
     * prevent public instance creation
     * Only can get instance via getInstance() method
     */
    private SharedData(){}

    /**
     *
     * @param paths to check if any operation is going on it
     * @return true if no operation is going on else false
     */
    public synchronized static boolean checkIfInRunningTask(ArrayList<String> paths){
        if(SharedData.CURRENT_RUNNING_OPERATIONS.size()<1){
            return false;
        }
        for (int i = 0; i < SharedData.CURRENT_RUNNING_OPERATIONS.size(); i++) {
            for (String p:paths) {
                Log.d("fucked", "checkIfInRunningTask: "+p);
                if(SharedData.CURRENT_RUNNING_OPERATIONS.get(i).equalsIgnoreCase(p)){
                    return true;
                }
            }
        }
        return false;
    }

}
