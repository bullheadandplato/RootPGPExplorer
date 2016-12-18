package com.cryptopaths.cryptofm.filemanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/17/16.
 * Data model to fill recycler view for folders
 */

public class DataModelFolders {
    private String folderName;
    private String folderEncryption;
    private int folderFileCount;
    private float folderSize;
    private List<String> fileNames=new ArrayList<>();
    private List<DataModelFiles> folderFiles=new ArrayList<>();

    public DataModelFolders(String foldername){
        this.folderName=foldername;
        this.folderEncryption=FileUtils.isEncryptedFolder(foldername);
        this.folderFileCount=FileUtils.getNumberOfFiles(foldername);
        this.folderSize=FileUtils.getFolderSize(foldername);
        try{
            this.fileNames=FileUtils.getFileNamesInAFolder(foldername);
            fillFiles();
        }catch (IllegalArgumentException e){
            this.fileNames=null;
        }
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
    private void fillFiles(){
        for (String filename:
             this.fileNames) {
            folderFiles.add(new DataModelFiles(filename));
        }
    }
    public DataModelFiles getFile(int position){
        return this.folderFiles.get(position);
    }

}
