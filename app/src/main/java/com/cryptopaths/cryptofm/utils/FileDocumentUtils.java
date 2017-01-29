package com.cryptopaths.cryptofm.utils;

import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.v4.provider.DocumentFile;

import com.cryptopaths.cryptofm.CryptoFM;

import java.text.DecimalFormat;

/**
 * Created by tripleheader on 1/25/17.
 * For the new type introduced by Google
 */

public class FileDocumentUtils {

    FileDocumentUtils() {

    }

    public static long getFileSize(String filename) {
        return DocumentFile.fromSingleUri(CryptoFM.getContext(), Uri.parse(filename)).length();
    }


    public static String getFileExtension(String filename) {
        String tmp= DocumentFile.fromSingleUri(CryptoFM.getContext(),Uri.parse(filename)).getType();
        if(tmp.indexOf('/')>0){
            //this means file contains right mime type
            tmp=tmp.substring(tmp.lastIndexOf('/'));
        }
        return tmp;
    }

    public static String getReadableSize(String filename) {
        long size=DocumentFile.fromSingleUri(CryptoFM.getContext(),Uri.parse(filename)).length();
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups      = (int) (Math.log10(size)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    public static boolean isEncryptedFile(String filename) {
        return false;
    }

    public static int getNumberofFiles(String filename) {
        return 0;
    }

    public static boolean isFile(String filename) {
        return false;
    }

    public static String getLastModifiedData(String filename) {
        return ""+DocumentFile.fromSingleUri(CryptoFM.getContext(),Uri.parse(filename)).lastModified();
    }

    public static boolean createFolder(String foldername) {
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
}
