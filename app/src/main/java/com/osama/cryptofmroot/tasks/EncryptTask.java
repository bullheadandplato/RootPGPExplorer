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

package com.osama.cryptofmroot.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.osama.cryptofmroot.encryption.EncryptionWrapper;
import com.osama.cryptofmroot.filemanager.listview.FileListAdapter;
import com.osama.cryptofmroot.filemanager.utils.SharedData;
import com.osama.cryptofmroot.filemanager.utils.UiUtils;
import com.osama.cryptofmroot.root.RootUtils;
import com.osama.cryptofmroot.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by home on 12/29/16.
 * encrypt file task
 */

public class EncryptTask extends AsyncTask<Void,String,String> {
    private ArrayList<String>       mFilePaths;
    private FileListAdapter         mAdapter;
    private MyProgressDialog        mProgressDialog;
    private Context                 mContext;
    private File                    pubKeyFile;
    private boolean                 isRootPath=false;

    private ArrayList<File>         mCreatedFiles=new ArrayList<>();
    private ArrayList<String>       mUnencryptedFiles=new ArrayList<>();
    private String                  mRootHandlingPath=SharedData.FILES_ROOT_DIRECTORY+"CryptoFM/enc";
    private File                    mRootHandlingFile;
    private String                  mCurrentPath;

    private static final String TAG                         = EncryptTask.class.getName();
    private static final String ENCRYPTION_SUCCESS_MESSAGE  = "Successfully encrypted files";

    public EncryptTask(Context context,FileListAdapter adapter,ArrayList<String> filePaths){
        this.mAdapter           = adapter;
        this.mContext           = context;
        this.mFilePaths         = filePaths;
        this.mProgressDialog    = new MyProgressDialog(context,"Encrypting",this);
        this.pubKeyFile         = new File(mContext.getFilesDir(),"pub.asc");
        mCurrentPath            = mAdapter.getmFileFiller().getCurrentPath();
        mRootHandlingPath       = mRootHandlingPath+mCurrentPath;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            if(RootUtils.isRootPath(mFilePaths.get(0))){
                mRootHandlingFile=new File(mRootHandlingPath);
                if(!mRootHandlingFile.mkdirs()){
                    Log.d(TAG, "doInBackground: cannot create directory");
                }
                isRootPath=true;
            }
            for (String path : mFilePaths) {
                if(!isCancelled()){
                    if(isRootPath){
                        path=path.replace(mCurrentPath,"");
                        RootUtils.copyFile(path,mRootHandlingPath);
                        path=mRootHandlingPath+path+"/";
                        Log.d(TAG, "doInBackground: path is: "+path);
                    }
                    File f = TasksFileUtils.getFile(path);
                        encryptFile(f);
                }

            }
            return ENCRYPTION_SUCCESS_MESSAGE;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error in encrypting file." + ex.getMessage();
        }
    }

    private void encryptFile(File f) throws Exception{
        if(!isCancelled()){
            if(f.isDirectory()){
                Log.d(TAG, "encryptFile: File is directory");
                for (File tmp: f.listFiles()) {
                    encryptFile(tmp);
                    mFilePaths.remove(f.getAbsolutePath().replace(mRootHandlingPath,""));
                }
            }else {
                File out = new File(f.getAbsolutePath()+".pgp");
                if(out.createNewFile()){
                    Log.d(TAG, "encryptFile: created file to encrypt into");
                }
                publishProgress(f.getName(),""+
                        ((FileUtils.getReadableSize((f.length())))));
                mCreatedFiles.add(out);
                mUnencryptedFiles.add(f.getAbsolutePath());

                if(out.canWrite()){
                    Log.d(TAG, "encryptFile: yes I can write");
                }
                EncryptionWrapper.encryptFile(f,out,pubKeyFile,true);
                if(isRootPath){
                    String path=out.getAbsolutePath().replace(mRootHandlingPath,"");
                    Log.d(TAG, "encryptFile: "+path.substring(0,path.lastIndexOf('.')));
                    RootUtils.deleteFile(path.substring(0,path.lastIndexOf('.')));
                    RootUtils.renameFile(out.getAbsolutePath(),path);
                }
            }
        }


    }

    @Override
    protected void onProgressUpdate(String... values) {
        mProgressDialog.setmProgressTextViewText(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        if(s.equals(ENCRYPTION_SUCCESS_MESSAGE)){
//            deleteAlertDialog();
            DeleteTask task=new DeleteTask(mContext,mAdapter,mUnencryptedFiles);
            task.setRunningFromEncryption(true);
            task.execute();

        }
        mProgressDialog.dismiss(s);
        Toast.makeText(
                mContext,
                s,
                Toast.LENGTH_SHORT
        ).show();

        if(isRootPath){
            RootUtils.deleteFile(mRootHandlingPath);
        }

    }

    private void deleteAlertDialog(){
        //ask the user if he/she wants to delete the unencrypted version of file
        AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
        dialog.setMessage("Do you want to delete the unencrypted version of folders?");
        dialog.setTitle("Delete unencrypted files");
        dialog.setPositiveButton("Yes, Sure!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                new DeleteTask(mContext,mAdapter,mUnencryptedFiles).execute();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UiUtils.reloadData(
                        mAdapter
                );
            }
        });
        dialog.show();
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }

    @Override
    protected void onCancelled() {
            for (File f : mCreatedFiles) {
                f.delete();
            }
        Toast.makeText(
                mContext,
                "Encryption canceled",
                Toast.LENGTH_SHORT
        ).show();
        SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        UiUtils.reloadData(
                mAdapter
        );
        Log.d("cancel","yes task is canceled");
        super.onCancelled();
    }

}
