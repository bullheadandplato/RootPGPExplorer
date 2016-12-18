package com.cryptopaths.cryptofm.filemanager;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cryptopaths.cryptofm.R;

/**
 * Created by tripleheader on 12/17/16.
 * Data model for the recyclerview
 */

public class DataModelFiles {
    private String fileName;
    private String fileEncryptionStatus;
    private String fileExtensionOrItems;
    private String fileSize;
    private Drawable fileIcon;

    public DataModelFiles(String filename, Context context) {
        this.fileName = filename;
        if(FileUtils.isFile(filename)){
            this.fileIcon=context.getDrawable(R.drawable.ic_insert_drive_file_white_48dp);
            this.fileExtensionOrItems=FileUtils.getExtension(filename);
            long size=FileUtils.getFileSize(filename);
            this.fileSize=FileUtils.getReadableSize(size);
            this.fileEncryptionStatus=FileUtils.isEncryptedFile(filename);
        }else{
            this.fileIcon=context.getDrawable(R.drawable.ic_folder_white_48dp);
            //in case of folder file extension will be number of items in folder
            this.fileExtensionOrItems=FileUtils.getNumberOfFiles(filename)+" items";
            this.fileEncryptionStatus=FileUtils.isEncryptedFolder(filename);
            //concatenated current path because recursively look folders and find size
            long size=FileUtils.getFolderSize(FileUtils.CURRENT_PATH+"/"+filename);
            this.fileSize=FileUtils.getReadableSize(size);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileEncryptionStatus() {
        return fileEncryptionStatus;
    }

    public String getFileExtension() {
        return fileExtensionOrItems;
    }

    public String getFileSize() {
        return fileSize;
    }
    public Drawable getFileIcon(){
        return this.fileIcon;
    }

}