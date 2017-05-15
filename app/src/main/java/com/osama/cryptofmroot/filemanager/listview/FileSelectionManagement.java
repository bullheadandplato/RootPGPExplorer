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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.utils.MimeType;
import com.osama.cryptofmroot.filemanager.utils.SharedData;
import com.osama.cryptofmroot.filemanager.utils.UiUtils;
import com.osama.cryptofmroot.root.RootUtils;
import com.osama.cryptofmroot.tasks.DecryptTask;
import com.osama.cryptofmroot.utils.FileUtils;

import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by tripleheader on 1/21/17.
 * manage the selection of files
 * opening the files etc
 */

public class FileSelectionManagement {
    private DataModelFiles   mDataModel;
    private FileListAdapter  mFileListAdapter;
    private Context          mContext;
    private Drawable         mSelectedFileIcon;
    private Drawable         mFolderIcon;
    private AdapterCallbacks clickCallBack;

    private static final String TAG="FileSelectionManagement";


    private ArrayList<Integer>      mSelectedPosition   = new ArrayList<>();
    private ArrayList<String>       mSelectedFilePaths  = new ArrayList<>();
    private FileFillerWrapper       mFileFiller;

    public FileSelectionManagement(Context context, FileListAdapter adapter){
        this.mContext     = context;
        mSelectedFileIcon = mContext.getDrawable(R.drawable.ic_check_circle_white_48dp);
        mFolderIcon       = mContext.getDrawable(R.drawable.ic_default_folder);
        clickCallBack     = (AdapterCallbacks)mContext;
        mFileListAdapter  =adapter;
        mFileFiller       =adapter.getmFileFiller();

    }

    public void selectAllFiles() {
        for (int i = 0; i < mFileFiller.getTotalFilesCount(); i++) {
            mDataModel = mFileFiller.getFileAtPosition(i);
            selectFile(i);
        }
        mFileListAdapter.notifyDataSetChanged();
    }


    void selectionOperation(int position, View view){
        mDataModel  = mFileFiller.getFileAtPosition(position);
        if(mDataModel.getSelected()){
            Log.d(TAG, "selectionOperation: fixing a bug in files selection");
            mSelectedFilePaths.remove(mDataModel.getFilePath());
            mDataModel.setSelected(false);
            mSelectedPosition.remove(Integer.valueOf(position));
            clickCallBack.decrementSelectionCount();
            if(mSelectedPosition.size()==1){
                clickCallBack.selectedFileType(!mFileFiller.getFileAtPosition(mSelectedPosition.get(0)).getFile());
            }
            if(mDataModel.getFile()){
                mDataModel.setFileIcon(MimeType.getIcon(mDataModel.getFileExtension()));
            }else{
                mDataModel.setFileIcon(mFolderIcon);
            }
            mDataModel.setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.white));
        }else{
            selectFile(position);
        }
        if(!(mSelectedPosition.size()<1)){
            mFileListAdapter.notifyItemChanged(position);
        }
    }
    private void selectFile(int position){
        if(!mDataModel.getSelected()) {
           mDataModel.setBackgroundColor(ContextCompat.getColor(CryptoFM.getContext(),R.color.cardSelectedColor));
            mSelectedPosition.add(position);
            mSelectedFilePaths.add(mDataModel.getFilePath());
            mDataModel.setFileIcon(mSelectedFileIcon);
            mDataModel.setSelected(true);
            clickCallBack.incrementSelectionCount();
            clickCallBack.selectedFileType(!mDataModel.getFile());

        }
    }
    void selectFileInSelectionMode(int position){
        if(mSelectedPosition.size()>0){
            mDataModel=mFileFiller.getFileAtPosition(mSelectedPosition.get(0));
            mDataModel.setSelected(false);
            mDataModel.setFileIcon(MimeType.getIcon(mDataModel.getFileExtension()));
            mFileListAdapter.notifyItemChanged(mSelectedPosition.get(0));
            mSelectedPosition.clear();
            mSelectedFilePaths.clear();
        }
            mDataModel=mFileFiller.getFileAtPosition(position);
            mSelectedPosition.add(position);
            mSelectedFilePaths.add(mDataModel.getFilePath());
            mDataModel.setFileIcon(mSelectedFileIcon);
            mDataModel.setSelected(true);
        mFileListAdapter.notifyItemChanged(position);
    }


    public void setmSelectionMode(Boolean value){
        if(value){
            return;
        }
        //first check if there are select files
        if(mSelectedPosition.size()>0) {
            for (Integer pos : mSelectedPosition) {
                mDataModel =  mFileFiller.getFileAtPosition(pos);
                mDataModel.setSelected(false);
            }
            mSelectedPosition.clear();
            mSelectedFilePaths.clear();
        }
    }



    public FileListAdapter getmFileListAdapter() {
        return mFileListAdapter;
    }

    public ArrayList<String> getmSelectedFilePaths() {
        return mSelectedFilePaths;
    }


    void openFile(final String filename) {
        if (SharedData.IS_IN_COPY_MODE) {
            return;
        }
        if (FileUtils.isEncryptedFile(filename)) {
            Log.d(TAG, "openFile: File name is: "+filename);
            if (SharedData.KEY_PASSWORD == null) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.password_dialog_layout);
                dialog.show();
                dialog.findViewById(R.id.cancel_decrypt_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText editText = (EditText) dialog.findViewById(R.id.key_password);
                dialog.findViewById(R.id.decrypt_file_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().length() < 1) {
                            editText.setError("please give me your encryption password");
                            return;
                        } else {
                            SharedData.KEY_PASSWORD = editText.getText().toString();
                            dialog.dismiss();
                            new DecryptTask(
                                    mContext,
                                    mFileListAdapter,
                                    SharedData.DB_PASSWORD,
                                    SharedData.USERNAME,
                                    filename,
                                    SharedData.KEY_PASSWORD).execute();
                        }

                    }
                });
            } else {
                new DecryptTask(
                        mContext,
                        mFileListAdapter,
                        SharedData.DB_PASSWORD,
                        SharedData.USERNAME,
                        filename,
                        SharedData.KEY_PASSWORD).execute();
            }

        }else{
            UiUtils.openFile(filename,mContext,mFileListAdapter);
        }
    }

    void openFolder(String filename,int position,View view) {
        if(SharedData.STARTED_IN_SELECTION_MODE) {
            mSelectedPosition.clear();
            mSelectedFilePaths.clear();
        }
        if(SharedData.SELECTION_MODE){
            selectionOperation(position,view);
            return;
        }
        clickCallBack.changeTitle(filename);
        clickCallBack.animateForward(filename);
    }
    void startSelectionMode(){
        SharedData.SELECTION_MODE = true;
        clickCallBack.onLongClick();
    }

}
