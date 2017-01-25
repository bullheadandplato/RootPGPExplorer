package com.cryptopaths.cryptofm.utils;

import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import com.cryptopaths.cryptofm.CryptoFM;

import java.text.DecimalFormat;

/**
 * Created by tripleheader on 1/25/17.
 * For the new type introduced by Google
 */

public class FileDocumentUtils implements FileOperations{

    @Override
    public long getFileSize(String filename) {
        return DocumentFile.fromSingleUri(CryptoFM.getContext(), Uri.parse(filename)).length();
    }

    @Override
    public String getFileExtension(String filename) {
        String tmp= DocumentFile.fromSingleUri(CryptoFM.getContext(),Uri.parse(filename)).getType();
        if(tmp.indexOf('/')>0){
            //this means file contains right mime type
            tmp=tmp.substring(tmp.lastIndexOf('/'));
        }
        return tmp;
    }

    @Override
    public String getReadableSize(String filename) {
        long size=DocumentFile.fromSingleUri(CryptoFM.getContext(),Uri.parse(filename)).length();
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups      = (int) (Math.log10(size)/Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    @Override
    public boolean isEncryptedFile(String filename) {
        return false;
    }

    @Override
    public int getNumberofFiles(String filename) {
        return 0;
    }

    @Override
    public boolean isFile(String filename) {
        return false;
    }

    @Override
    public String getLastModifiedData(String filename) {
        String date = ""+DocumentFile.fromSingleUri(CryptoFM.getContext(),Uri.parse(filename)).lastModified();
        return date;
    }

    @Override
    public boolean createFolder(String foldername) {
        return false;
    }

    @Override
    public void deleteDecryptedFolder() {

    }

    @Override
    public boolean getFile(String filename) {
        return false;
    }

    @Override
    public boolean checkReadStatus(String filename) {
        return false;
    }
}
