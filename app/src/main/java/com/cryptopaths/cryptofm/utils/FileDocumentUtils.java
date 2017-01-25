package com.cryptopaths.cryptofm.utils;

import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import com.cryptopaths.cryptofm.CryptoFM;

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
        return null;
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
