package com.cryptopaths.cryptofm.filemanager;


import java.util.ArrayList;

/**
 * Created by tripleheader on 12/17/16.
 * Data model for the recyclerview
 */

public class DataModelFiles {
    private String fileName;
    private String fileEncryptionStatus;
    private String fileExtension;
    private float fileSize;

    public DataModelFiles(String filename) {
        this.fileName = filename;
        this.fileEncryptionStatus = FileUtils.isEncryptedFile(filename);
        this.fileExtension = FileUtils.getExtension(filename);
        this.fileSize=FileUtils.getFileSize(filename);
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileEncryptionStatus() {
        return fileEncryptionStatus;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public float getFileSize() {
        return fileSize;
    }

}