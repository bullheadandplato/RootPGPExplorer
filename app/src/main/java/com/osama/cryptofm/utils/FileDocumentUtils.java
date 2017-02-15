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

import android.net.Uri;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.osama.cryptofm.CryptoFM;
import com.osama.cryptofm.filemanager.utils.SharedData;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by tripleheader on 1/25/17.
 * For the new type introduced by Google
 */

public class FileDocumentUtils {
    private static final String TAG=FileDocumentUtils.class.getName();
    FileDocumentUtils() {

    }

    public static long getFileSize(String filename) {
        return DocumentFile.fromTreeUri(CryptoFM.getContext(), Uri.parse(filename)).length();
    }


    public static String getFileExtension(String filename) {
        String tmp= DocumentFile.fromTreeUri(CryptoFM.getContext(),Uri.parse(filename)).getType();
        if(tmp.indexOf('/')>0){
            //this means file contains right mime type
            tmp=tmp.substring(tmp.lastIndexOf('/'));
        }
        return tmp;
    }

    public static String getReadableSize(String filename) {
        long size=DocumentFile.fromTreeUri(CryptoFM.getContext(),Uri.parse(filename)).length();
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups      = (int) (Math.log10(size)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    public static boolean isEncryptedFile(String filename) {
        return false;
    }

    public static int getNumberofFiles(File file) {
        return getDocumentFile(file).listFiles().length;
    }

    public static boolean isFile(String filename) {
        return false;
    }

    public static String getLastModifiedData(String filename) {
        return ""+getDocumentFile(new File(filename)).lastModified();
    }

    public static boolean createFolder(String currentPath,String foldername) {
        getDocumentFile(new File(currentPath)).createDirectory(foldername);
        return false;
    }

    public static void deleteDecryptedFolder() {

    }

    public static boolean getFile(String filename) {
        return false;
    }

    public static boolean checkReadStatus(String filename) {
        return false;
    }


    public static DocumentFile getDocumentFile(final File file) {
        String baseFolder = SharedData.EXTERNAL_SDCARD_ROOT_PATH;
        boolean isDirectory=file.isDirectory();
        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            Log.d(TAG, "getDocumentFile: basefolder:file"+baseFolder+" : "+file.getAbsolutePath());
            String fullPath = file.getCanonicalPath();
            if((file.getAbsolutePath()+"/").equals(baseFolder)){
                return DocumentFile.fromTreeUri(CryptoFM.getContext(),Uri.parse(SharedData.EXT_ROOT_URI));
            }else{

                relativePath = fullPath.substring(baseFolder.length());
            }
        }
        catch (IOException e) {
            return null;
        }

        Uri treeUri = Uri.parse(SharedData.EXT_ROOT_URI);

        if (treeUri == null) {
            return null;
        }
        relativePath=relativePath.replace(SharedData.EXTERNAL_SDCARD_ROOT_PATH,"");
        // start with root of SD card and then parse through document tree.
        Log.d(TAG, "getDocumentFile: relative path is: "+relativePath);
        DocumentFile document = DocumentFile.fromTreeUri(CryptoFM.getContext(), treeUri);

        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);
            Log.d(TAG, "getDocumentFile: next document is:  "+parts[i]);
            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    Log.d(TAG, "getDocumentFile: creating next directory");
                    nextDocument = document.createDirectory(parts[i]);
                }
                else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }
        Log.d(TAG, "getDocumentFile: file uri is: "+document.getUri());
        return document;
    }
}
