/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.filemanager.listview;


import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.slownet5.pgprootexplorer.CryptoFM;
import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.utils.MimeType;
import com.slownet5.pgprootexplorer.utils.FileUtils;

import java.io.File;

/**
 * Created by tripleheader on 12/17/16.
 * Data model for the recyclerview
 */

public class DataModelFiles implements Parcelable {
    private String      fileName;
    private String      fileExtensionOrItems;
    private String fileDate;
    private Drawable    fileIcon;

    private Boolean     isSelected   = false;
    private Boolean     isFile       = false;
    private boolean     isEncrypted  = false;
    private String      mFilePath;
    private int       backgroundColor;

    public DataModelFiles(){
        backgroundColor= ContextCompat.getColor(CryptoFM.getContext(),R.color.white);
    }
    public DataModelFiles(File file) {
        this.fileName   = file.getName();
        this.mFilePath  = file.getPath();
        //check if i can read file
        backgroundColor= ContextCompat.getColor(CryptoFM.getContext(),R.color.white);
        if(FileUtils.isFile(file)){
            Log.d("rootF", "DataModelFiles: file name is: "+file.getName());
            //this.fileIcon=CryptoFM.getContext().getDrawable(R.drawable.ic_insert_drive);
            this.fileExtensionOrItems=FileUtils.getExtension(file.getName());
            this.fileIcon=MimeType.getIcon(fileExtensionOrItems);
            long size=FileUtils.getFileSize(file);
            this.fileDate =FileUtils.getReadableSize(size);
            this.isEncrypted=FileUtils.isEncryptedFile(file.getName());
            this.isFile=true;
        }else {
            this.fileIcon = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_default_folder);
            //in case of folder file extension will be number of items in folder
            this.fileExtensionOrItems = FileUtils.getNumberOfFiles(file) + " items";
            this.isEncrypted = false;
            this.fileDate = FileUtils.getLastModifiedDate(file);
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExtension() {
        return fileExtensionOrItems;
    }

    public String getFileDate() {
        return fileDate;
    }
    public Drawable getFileIcon(){
        return this.fileIcon;
    }

    public boolean isEncrypted() {
        return isEncrypted;
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
        return mFilePath;
    }

    public Boolean getFile() {
        return isFile;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileExtensionOrItems(String fileExtensionOrItems) {
        this.fileExtensionOrItems = fileExtensionOrItems;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public void setFile(Boolean file) {
        isFile = file;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}