package com.cryptopaths.cryptofm.filemanager;

/**
 * Created by tripleheader on 12/17/16.
 */

public class DataModelFolders {
    private String folderName;
    private String folderEncryption;
    private int folderFileCount;
    private float folderSize;
    public DataModelFolders(String foldername){
        this.folderName=foldername;
        this.folderEncryption=FileUtils.isEncryptedFolder(foldername);
        this.folderFileCount=FileUtils.getNumberOfFiles(foldername);
        this.folderSize=FileUtils.getFolderSize(foldername);
    }

    public String getFolderName() {
        return folderName;
    }


    public String getFolderEncryption() {
        return folderEncryption;
    }



    public int getFolderFileCount() {
        return folderFileCount;
    }



    public float getFolderSize() {
        return folderSize;
    }

}
