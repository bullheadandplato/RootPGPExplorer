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

package com.osama.cryptofm.utils;

import android.os.Environment;
import android.util.Log;

import com.osama.cryptofm.filemanager.utils.SharedData;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/17/16.
 * the file related operations
 */

public class FileUtils {
    private static final String TAG     = "files";
    public static  String CURRENT_PATH  = " ";
    public static String CONTENT_URI;

    public static long getFileSize(String filename){
        return new File(CURRENT_PATH+filename).length();
    }

    public static long getFolderSize(String folderPath) {
        File dir  = new File(folderPath);
        long size = 0;

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                size += file.length();
            }
            else
                size += getFolderSize(file.getPath());
        }
        return size;
    }
    public static boolean isEncryptedFolder(String filename){
        File dir = new File(CURRENT_PATH+filename);
        //if file is not a directory but just a file
        if(dir.isFile()){
            return dir.getName().contains("pgp");
        }
        //if all the files in folder are encrypted than this variable will be zero
        if(dir.listFiles().length<1){
            return false;
        }
        int temp=dir.listFiles().length;
        for (File f:
                dir.listFiles()) {
            if(f.getName().contains("pgp")){
                temp--;
            }else{
                temp++;
            }
        }
        return temp == 0;
    }

    public static boolean isEncryptedFile(String filename){
        return filename.contains(".pgp");
    }

    public static int getNumberOfFiles(String  foldername){
        Log.d(TAG, "getNumberOfFiles: "+CURRENT_PATH+foldername);
        File file=new File(CURRENT_PATH+foldername);
        if(file.canRead()){
            return file.listFiles().length;
        }else{
            return 0;
        }

    }

    public static String getExtension(String fileName){
        final String emptyExtension = "file";
        if(fileName == null){
            return emptyExtension;
        }
        int index = fileName.lastIndexOf(".");
        if(index == -1){
            return emptyExtension;
        }
        return fileName.substring(index + 1);
    }

    public static List<String> getFileNamesInAFolder(String foldername) throws IllegalArgumentException{
        List<String> result=new ArrayList<>();
        File folder=new File(CURRENT_PATH+foldername);

        File[] files=folder.listFiles();
        if(files.length>0){
            for (File f:
                 files) {
                result.add(f.getName());
            }

        } else{
            throw new IllegalArgumentException("folder is empty");
        }
        return result;
    }

    public static Boolean isFile(String filename) {
        return new File(CURRENT_PATH + filename).isFile();
    }

    public static String getReadableSize(long size) {
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups      = (int) (Math.log10(size)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }
    public static String getLastModifiedDate(String filename){
        String date = new Date(new File(CURRENT_PATH+filename).lastModified()).toString();
        Log.d(TAG, "getLastModifiedDate: date is: "+date);
        return date;

    }


    public static Boolean createFolder(String folderName) {
        File temp = new File(CURRENT_PATH+folderName);
        if(temp.exists()){
            return false;
        }else{
            return temp.mkdirs();
        }
    }

    public static void deleteDecryptedFolder() {

        File f=new File(Environment.getExternalStorageDirectory().getPath(),"decrypted");
        if(f.exists()){
            for (File child:
                 f.listFiles()) {
                child.delete();
            }
            if(!f.delete()){
                Log.d(TAG, "deleteDecryptedFolder: cannot delete decrypted folder");
            }
        }
    }
    public static File getFile(String filename){
        
        return new File(CURRENT_PATH+filename);
    }
    public static boolean checkReadStatus(String filename){
        return new File(FileUtils.CURRENT_PATH+filename).canRead();
    }

    public static boolean isSdCardPath(String path){
        Log.d(TAG, "isSdCardPath: Yes entering sdcard path");
        if(SharedData.EXTERNAL_SDCARD_ROOT_PATH!=null && SharedData.EXT_ROOT_URI!=null){
            if(CURRENT_PATH.contains(SharedData.EXTERNAL_SDCARD_ROOT_PATH)){
                convertPathToUri(path);
                return true;
            }
        }
        return false;
    }
    private static String convertPathToUri(String path){
        //change the path and make it content uri
        CONTENT_URI=CURRENT_PATH+path;
        Log.d(TAG, "convertPathToUri: URI jkhjkis: "+CONTENT_URI);
        return CONTENT_URI;

    }
    public static boolean isDocumentFile(String path){
        return SharedData.EXTERNAL_SDCARD_ROOT_PATH!=null
                && path.contains(SharedData.EXTERNAL_SDCARD_ROOT_PATH);
    }
}
