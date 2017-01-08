package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;

import com.cryptopaths.cryptofm.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/18/16.
 * fill the map of files against path
 */

public  class FileFillerWrapper {
    private static List<DataModelFiles> allFiles   = new ArrayList<>();
    private static int totalFilesCount             = 0;

    private static String currentPath;


    static void fillData(String current, Context context){
        currentPath=current;
        //be sure to change the path in File utilities
        FileUtils.CURRENT_PATH = currentPath;
        //for each file in current path fill data
        File file              = new File(currentPath);
        totalFilesCount=file.list().length;
        if(file.list().length>0){
            allFiles.clear();
            for (File f:
                    file.listFiles()) {
                allFiles.add(new DataModelFiles(f.getName(),context));
            }
        }
    }
    public static DataModelFiles getFileAtPosition(int position){
        return allFiles.get(position);
    }

    public static int getTotalFilesCount() {
        return totalFilesCount;
    }

    public static String getCurrentPath() {
        return currentPath;
    }
}
