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

package com.osama.cryptofmroot.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.osama.cryptofmroot.CryptoFM;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;


/**
 * Created by tripleheader on 12/17/16.
 * the file related operations
 */

public class FileUtils {
    private static final String TAG     = "files";
    public static final Uri FILE_URI = MediaStore.Files.getContentUri("external");

    public static long getFileSize(File file){
        return file.length();
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
    public static boolean isEncryptedFolder(File dir){
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
        return filename.contains(".pgp") || filename.contains(".gpg");
    }

    public static int getNumberOfFiles(File file ){
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


    public static boolean isFile(File file) {
        return file.isFile();
    }

    public static String getReadableSize(long size) {
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups      = (int) (Math.log10(size)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }
    public static String getLastModifiedDate(File file){
        return new Date(file.lastModified()).toString();
    }


    public static boolean createFolder(String folderName) {
        File temp = new File(folderName);
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
        Log.d(TAG, "getFile: getting file: "+filename);
        return new File(filename);
    }
    public static boolean checkReadStatus(File f){
        return f.canRead();
    }
    public static void notifyChange(Context ctx,String... path){
        Log.d(TAG, "notifyChange: notifying change");
        MediaScannerConnection.scanFile(ctx, path, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG, "onScanCompleted: scan completed: "+path);
            }
        });
    }

    public static void removeMediaStore(Context context, File file) {
        try {
            final ContentResolver resolver = context.getContentResolver();

            // Remove media store entries for any files inside this directory, using
            // path prefix match. Logic borrowed from MtpDatabase.
            if (file.isDirectory()) {
                final String path = file.getAbsolutePath() + "/";
                resolver.delete(FILE_URI,
                        "_data LIKE ?1 AND lower(substr(_data,1,?2))=lower(?3)",
                        new String[] { path + "%", Integer.toString(path.length()), path });
            }

            // Remove media store entry for this exact file.
            final String path = file.getAbsolutePath();
            resolver.delete(FILE_URI,
                    "_data LIKE ?1 AND lower(_data)=lower(?2)",
                    new String[] { path, path });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Uri getUri(String filePath){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                    CryptoFM.getContext(),
                    CryptoFM.getContext().getApplicationContext().getPackageName() + ".provider",
                    FileUtils.getFile(filePath)
            );
        }else{
            return Uri.fromFile(FileUtils.getFile(filePath));
        }
    }
}
