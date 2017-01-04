package com.cryptopaths.cryptofm.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.math.BigDecimal;
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
    public static String isEncryptedFolder(String filename){
        File dir = new File(CURRENT_PATH+filename);
        //if file is not a directory but just a file
        if(dir.isFile()){
            if(dir.getName().contains("pgp")){
                return "Encrypted";
            }else{
                return "Not encrypted";
            }
        }
        //if all the files in folder are encrypted than this variable will be zero
        if(dir.listFiles().length<1){
            return "Cannot see";
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
        if(temp==0){
            return "Encrypted";
        }else{
            return "Not Encrypted";
        }
    }

    public static String isEncryptedFile(String filename){
        if(filename.contains(".pgp")){
            return "Encrypted";
        }else{
            return "Not encrypted";
        }
    }

    public static int getNumberOfFiles(String  foldername){
        Log.d(TAG, "getNumberOfFiles: "+CURRENT_PATH+foldername);
        return new File(CURRENT_PATH+foldername).list().length;
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

    public static Boolean isFile(String filename){
        return new File(CURRENT_PATH+filename).isFile();
    }
    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace the numbers of decimals
     * @return
     */
    private static float round(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
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
            if(temp.mkdir()){
                return true;
            }else{
                return false;
            }
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
}
