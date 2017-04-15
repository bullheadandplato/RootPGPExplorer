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

package com.osama.cryptofmroot.filemanager.listview;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.utils.MimeType;
import com.osama.cryptofmroot.utils.FileUtils;

import java.io.File;

/**
 * Created by tripleheader on 12/17/16.
 * Data model for the recyclerview
 */

public class DataModelFiles  {
    private String      fileName;
    private String      fileExtensionOrItems;
    private String      fileSize;
    private Drawable    fileIcon;

    private Boolean     isSelected   = false;
    private Boolean     isFile       = false;
    private boolean     isEncrypted  = false;
    private String      mFilePath;

    private static MimeType mIconManager;
    public DataModelFiles(File file) {
        if(mIconManager==null){
            mIconManager=new MimeType(CryptoFM.getContext());
        }
        this.fileName   = file.getName();
        this.mFilePath  = file.getPath();
        //check if i can read file
        if(FileUtils.isFile(file)){
            this.fileIcon=CryptoFM.getContext().getDrawable(R.drawable.ic_insert_drive);
            this.fileExtensionOrItems=FileUtils.getExtension(file.getName());
            this.fileIcon=MimeType.getIcon(fileExtensionOrItems);
            long size=FileUtils.getFileSize(file);
            this.fileSize=FileUtils.getReadableSize(size);
            this.isEncrypted=FileUtils.isEncryptedFile(file.getName());
            this.isFile=true;
        }else {
            this.fileIcon = CryptoFM.getContext().getDrawable(R.drawable.ic_default_folder);
            //in case of folder file extension will be number of items in folder
            this.fileExtensionOrItems = FileUtils.getNumberOfFiles(file) + " items";
            this.isEncrypted = FileUtils.isEncryptedFolder(file);
            this.fileSize = FileUtils.getLastModifiedDate(file);
        }
    }


    public String getFileName() {
        return fileName;
    }

    public Drawable getFileEncryptionStatus() {
        if(isEncrypted){
            return CryptoFM.getContext().getDrawable(R.drawable.ic_encrypt);
        }else{
            return CryptoFM.getContext().getDrawable(R.drawable.ic_decrypt);
        }
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
        return mFilePath;
    }

    public Boolean getFile() {
        return isFile;
    }
}