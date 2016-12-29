package com.cryptopaths.cryptofm.filemanager;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.utils.FileUtils;

/**
 * Created by tripleheader on 12/17/16.
 * Data model for the recyclerview
 */

public class DataModelFiles {
    private String      fileName;
    private String      fileEncryptionStatus;
    private String      fileExtensionOrItems;
    private String      fileSize;
    private Drawable    fileIcon;

    private Boolean     isSelected = false;
    private Boolean     isFile     = false;

    public DataModelFiles(String filename, Context context) {
        this.fileName = filename;
        if(FileUtils.isFile(filename)){
            this.fileIcon=context.getDrawable(R.drawable.ic_insert_drive_file_white_48dp);
            this.fileExtensionOrItems=FileUtils.getExtension(filename);
            long size=FileUtils.getFileSize(filename);
            this.fileSize=FileUtils.getReadableSize(size);
            this.fileEncryptionStatus=FileUtils.isEncryptedFile(filename);
            this.isFile=true;
        }else{
            this.fileIcon=context.getDrawable(R.drawable.ic_folder_white_48dp);
            //in case of folder file extension will be number of items in folder
            this.fileExtensionOrItems=FileUtils.getNumberOfFiles(filename)+" items";
            this.fileEncryptionStatus=FileUtils.isEncryptedFolder(filename);
            this.fileSize=FileUtils.getLastModifiedDate(filename);
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
    public void setFileIcon(Drawable drawable){
        this.fileIcon=drawable;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
    public String getFilePath(){
        return FileUtils.CURRENT_PATH+this.fileName;
    }

    public Boolean getFile() {
        return isFile;
    }
}