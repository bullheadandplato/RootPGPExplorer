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

package com.osama.cryptofm.filemanager.listview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.osama.cryptofm.R;
import com.osama.cryptofm.filemanager.utils.MimeType;
import com.osama.cryptofm.filemanager.utils.SharedData;
import com.osama.cryptofm.tasks.DecryptTask;
import com.osama.cryptofm.utils.FileDocumentUtils;
import com.osama.cryptofm.utils.FileUtils;

import java.util.ArrayList;

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


    void selectionOperation(int position){
        mDataModel  = mFileFiller.getFileAtPosition(position);

        if(mDataModel.getSelected()){
            Log.d(TAG, "selectionOperation: fixing a bug in files selection");
            mSelectedFilePaths.remove(mDataModel.getFilePath());
            mDataModel.setSelected(false);
            clickCallBack.decrementSelectionCount();
            if(mDataModel.getFile()){
                mDataModel.setFileIcon(MimeType.getIcon(mDataModel.getFileExtension()));
            }else{
                mDataModel.setFileIcon(mFolderIcon);
            }
        }else{
            selectFile(position);
        }

        mFileListAdapter.notifyItemChanged(position);

    }
    private void selectFile(int position){
        if(!mDataModel.getSelected()) {
            mSelectedPosition.add(position);
            mSelectedFilePaths.add(mDataModel.getFilePath());
            mDataModel.setFileIcon(mSelectedFileIcon);
            mDataModel.setSelected(true);
            clickCallBack.incrementSelectionCount();
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

    public void resetFileIcons(){
        for (Integer pos:
                mSelectedPosition) {
            mDataModel = mFileFiller.getFileAtPosition(pos);
            if(mDataModel.getFile()){
                mDataModel.setFileIcon(MimeType.getIcon(mDataModel.getFileExtension()));
            }else{
                mDataModel.setFileIcon(mFolderIcon);
            }
            mFileListAdapter.notifyItemChanged(pos);

        }
    }

    public ArrayList<String> getmSelectedFilePaths() {
        return mSelectedFilePaths;
    }


    void openFile(final String filename) {
        if (SharedData.IS_IN_COPY_MODE) {
            return;
        }
        if (FileUtils.getExtension(filename).equals("pgp")) {
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
                                    SharedData.DB_PASSWWORD,
                                    SharedData.USERNAME,
                                    FileUtils.CURRENT_PATH+filename,
                                    SharedData.KEY_PASSWORD).execute();
                        }

                    }
                });
            } else {
                new DecryptTask(
                        mContext,
                        mFileListAdapter,
                        SharedData.DB_PASSWWORD,
                        SharedData.USERNAME,
                        FileUtils.CURRENT_PATH+filename,
                        SharedData.KEY_PASSWORD).execute();
            }

        } else {
            //open file
            if(SharedData.EXTERNAL_SDCARD_ROOT_PATH!=null &&
                    FileUtils.CURRENT_PATH.contains(SharedData.EXTERNAL_SDCARD_ROOT_PATH)){
                //open the document file
                DocumentFile file= FileDocumentUtils.getDocumentFile(FileUtils.getFile(filename));
                Intent intent = new Intent();
                intent.setDataAndType(file.getUri(),file.getType());
                intent.setAction(Intent.ACTION_VIEW);
                Intent x=Intent.createChooser(intent,"Open with");
                mContext.startActivity(x);
                return;
            }
            String mimeType =
                    MimeTypeMap.getSingleton().
                            getMimeTypeFromExtension(
                                    FileUtils.getExtension(filename
                                    )
                            );

            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = FileProvider.getUriForFile(
                        mContext,
                        mContext.getApplicationContext().getPackageName() + ".provider",
                        FileUtils.getFile(filename)
                );
                intent.setDataAndType(uri, mimeType);
            } else {
                intent.setDataAndType(Uri.fromFile(FileUtils.getFile(filename)), mimeType);
            }
            intent.setAction(Intent.ACTION_VIEW);
            Intent x = Intent.createChooser(intent, "Open with: ");
            mContext.startActivity(x);
        }
    }

    void openFolder(String filename,int position) {
        if(SharedData.STARTED_IN_SELECTION_MODE) {
            mSelectedPosition.clear();
            mSelectedFilePaths.clear();
        }
        if(SharedData.SELECTION_MODE){
            selectionOperation(position);
            return;
        }
        String folderPath = mFileFiller.getCurrentPath() + filename + "/";
        clickCallBack.changeTitle(folderPath);
        mFileFiller.fillData(folderPath, mContext);
        mFileListAdapter.notifyDataSetChanged();
        if (mFileFiller.getTotalFilesCount() < 1) {
            clickCallBack.showNoFilesFragment();
        }
    }
    void startSelectionMode(){
        SharedData.SELECTION_MODE = true;
        clickCallBack.onLongClick();
    }
}
