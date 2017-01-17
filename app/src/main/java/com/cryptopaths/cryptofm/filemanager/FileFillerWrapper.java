package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.util.Log;

import com.cryptopaths.cryptofm.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/18/16.
 * fill the map of files against path
 */

public  class FileFillerWrapper {
    private static List<DataModelFiles> allFilesDecrypted   = new ArrayList<>();
    private static List<DataModelFiles> allFilesEncrypted   = new ArrayList<>();
    private static int totalFilesCount             = 0;

    private static String currentPath;


    static void fillData(String current, Context context){
        currentPath=current;
        //be sure to change the path in File utilities
        FileUtils.CURRENT_PATH = currentPath;
        //for each file in current path fill data
        File file              = new File(currentPath);
        totalFilesCount=file.list().length;
        DataModelFiles dm;
        if(file.list().length>0){
            allFilesDecrypted.clear();
            allFilesEncrypted.clear();
            for (File f:
                    file.listFiles()) {
                dm=new DataModelFiles(f.getName(),context);
                if(dm.isEncrypted()){
                    allFilesEncrypted.add(dm);
                }else{
                    allFilesDecrypted.add(dm);
                }
            }
            allFilesDecrypted=sortData(allFilesDecrypted);
            allFilesEncrypted=sortData(allFilesEncrypted);
        }
    }
    public static DataModelFiles getEncryptedFileAtPosition(int position){
        return allFilesEncrypted.get(position);
    }
    public static DataModelFiles getDecryptedFileAtPosition(int position){
        Log.d("position", "getDecryptedFileAtPosition: returning file at: "+position);
        return allFilesDecrypted.get(position);
    }

    public static int getTotalFilesCount() {
        return totalFilesCount;
    }
    public static int getDecryptedFileStartingPosition(){
        return allFilesEncrypted.size();
    }
    public static DataModelFiles getFileAtPosition(int position){
        if(position>allFilesEncrypted.size()){
            return allFilesDecrypted.get(position-allFilesEncrypted.size()-1);
        }else{
            return allFilesEncrypted.get(position);
        }
    }

    public static String getCurrentPath() {
        return currentPath;
    }
    private static List<DataModelFiles> sortData(List<DataModelFiles> list){
        DataModelFiles md;
        int size=list.size();
        for (int i = 0; i < size ; i++) {
            md=list.get(i);
            if(!md.getFile()){
                list.remove(md);
                list.add(0,md);
            }
        }
        return list;
    }
}
