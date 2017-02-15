package com.osama.cryptofm.filemanager.listview;

import android.content.Context;
import android.util.Log;

import com.osama.cryptofm.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/18/16.
 * fill the map of files against path
 */

public  class FileFillerWrapper {
    private static final String TAG = "FileFillerWrapper";
    private  List<DataModelFiles> allFiles   = new ArrayList<>();
    private  int totalFilesCount             = 0;

    private  String currentPath;


    public  void fillData(String current, Context context){
        currentPath=current;
        //be sure to change the path in File utilities
        FileUtils.CURRENT_PATH = currentPath;
        //for each file in current path fill data
        File file              = new File(currentPath);
        if(FileUtils.checkReadStatus("")){
            Log.d(TAG, "fillData: Cannot read files");
            totalFilesCount=0;
            if(file.list().length>0){
                allFiles.clear();
                for (File f:
                        file.listFiles()) {
                    //only add file which I can read
                    if(FileUtils.checkReadStatus(f.getName())){
                        allFiles.add(new DataModelFiles(f.getName(),context));
                        totalFilesCount++;
                    }
                }
                sortData();
            }
        }

    }
    public  DataModelFiles getFileAtPosition(int position){
        return allFiles.get(position);
    }

    public  int getTotalFilesCount() {
        return totalFilesCount;
    }

    public  String getCurrentPath() {
        return currentPath;
    }
    private void sortData(){
        DataModelFiles md;
        int size=allFiles.size();
        for (int i = 0; i < size ; i++) {
            md=allFiles.get(i);
            if(!md.getFile()){
                allFiles.remove(md);
                allFiles.add(0,md);
            }
        }
    }
}
