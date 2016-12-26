package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/18/16.
 * fill the map of files against path
 */

public class FileFillerWrapper {
    private List<DataModelFiles> allFiles   = new ArrayList<>();
    private int totalFilesCount             = 0;

    private String currentPath;

    public FileFillerWrapper(String currentPath, Context context) {
        this.currentPath = currentPath;
        //be sure to change the path in File utilities
        FileUtils.CURRENT_PATH = currentPath;
        //for each file in current path fill data
        File file              = new File(currentPath);
        totalFilesCount=file.list().length;
        if(file.list().length>0){
            for (File f:
                 file.listFiles()) {
                allFiles.add(new DataModelFiles(f.getName(),context));
            }
        }
    }
    public DataModelFiles getFileAtPosition(int position){
        return allFiles.get(position);
    }

    public int getTotalFilesCount() {
        return totalFilesCount;
    }

    public String getCurrentPath() {
        return currentPath;
    }
}
