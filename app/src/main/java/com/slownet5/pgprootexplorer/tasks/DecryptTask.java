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

package com.slownet5.pgprootexplorer.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.slownet5.pgprootexplorer.encryption.DatabaseHandler;
import com.slownet5.pgprootexplorer.encryption.EncryptionWrapper;
import com.slownet5.pgprootexplorer.filemanager.listview.FileListAdapter;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.filemanager.utils.UiUtils;
import com.slownet5.pgprootexplorer.root.RootUtils;
import com.slownet5.pgprootexplorer.utils.FileUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by tripleheader on 1/2/17.
 * file decryption task
 */

public class DecryptTask extends AsyncTask<Void,String,String> {
    private ArrayList<String>   mFilePaths;
    private FileListAdapter     mAdapter;
    private MyProgressDialog    mProgressDialog;
    private Context             mContext;
    private InputStream         mSecKey;
    private String              mDbPassword;
    private String              mUsername;
    private String              mFileName=null;
    private File                mPubKey;
    private char[]              mKeyPass;
    private String              rootPath;
    private ProgressDialog      singleModeDialog;
    private String              destFilename;
    private File                mRootHandlingFile;
    private String              mCurrentPath;

    private ArrayList<File>     mCreatedFiles=new ArrayList<>();
    private boolean             singleFileMode=false;
    private String              mRootHandlingPath= SharedData.FILES_ROOT_DIRECTORY+"CryptoFM/dec";

    private static final String TAG                        = DecryptTask.class.getName();
    private static final String DECRYPTION_SUCCESS_MESSAGE = "Decryption successful";

    private boolean isRootPath=false;

    public DecryptTask(Context context,FileListAdapter adapter,
                       ArrayList<String> filePaths,
                       String DbPass,String mUsername,String keypass){
        this.mContext           = context;
        this.mAdapter           = adapter;
        this.mFilePaths         = filePaths;
        this.mUsername          = mUsername;
        this.mKeyPass           = keypass.toCharArray();
        this.mDbPassword        = DbPass;
        this.mSecKey            = getSecretKey();
        this.mProgressDialog    = new MyProgressDialog(mContext,"Decrypting",this);
        this.mPubKey            = new File(mContext.getFilesDir(),"pub.asc");
        this.mCurrentPath       = mAdapter.getmFileFiller().getCurrentPath();
        this.mRootHandlingPath  = mRootHandlingPath+mAdapter.getmFileFiller().getCurrentPath();


    }
    public DecryptTask(Context context, FileListAdapter adapter,String DbPass,String mUsername,String filename,String keypass){
        this.mContext           = context;
        this.mAdapter           = adapter;
        this.mFileName          = filename;
        this.mDbPassword        = DbPass;
        this.mKeyPass           = keypass.toCharArray();
        this.mUsername          = mUsername;
        this.mSecKey            = getSecretKey();
        this.singleModeDialog   = new ProgressDialog(mContext);
        this.singleFileMode     = true;
        this.mCurrentPath       = mAdapter.getmFileFiller().getCurrentPath();
        this.mRootHandlingPath  = mRootHandlingPath+mAdapter.getmFileFiller().getCurrentPath();

    }
    @Override
    protected String doInBackground(Void... voids) {

        try{
            File root= new File(Environment.getExternalStorageDirectory(),"decrypted");
            if(!root.exists()){
                if(!root.mkdir()){
                    return "cannot decrypt file";
                }
            }
            rootPath=root.getPath();
            if(mFileName==null){
                    //do the normal files decryption
                try {
                    if(RootUtils.isRootPath(mFilePaths.get(0))){
                        mRootHandlingFile=new File(mRootHandlingPath);
                        if(mRootHandlingFile.mkdirs()){
                            Log.d(TAG, "doInBackground: yes I have capability");
                        }
                        isRootPath=true;
                    }
                    performNormalFormDecryption();
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getLocalizedMessage();
                }

            }else{
                if(RootUtils.isRootPath(mFileName)){
                    mRootHandlingFile=new File(mRootHandlingPath);
                    mRootHandlingFile.mkdirs();
                    RootUtils.copyFile(mFileName,mRootHandlingPath);
                    mFileName=mRootHandlingPath+mFileName;
                    isRootPath=true;
                }
                Log.d(TAG, "doInBackground: Filename is: "+mFileName);
                File in= TasksFileUtils.getFile(mFileName);
                File out= TasksFileUtils.getFile(root.getPath() + "/" + in.getName().substring(0, in.getName().lastIndexOf('.')));
                destFilename=out.getAbsolutePath();
                mSecKey=getSecretKey();
                EncryptionWrapper.decryptFile(in,out,mPubKey,getSecretKey(),mKeyPass);
                if(isRootPath){
                    in.delete();
                    FileUtils.notifyChange(mContext,in.getAbsolutePath());
                }
            }

        }catch (Exception ex){
            //let the activity know that password is incorrect and don't save it
            SharedData.KEY_PASSWORD=null;
            ex.printStackTrace();
            return ex.getMessage();
        }

        return DECRYPTION_SUCCESS_MESSAGE;
    }
    private ArrayList<String> tmp=new ArrayList<>();

    private ArrayList<String> getOnlyEncryptedFiles(ArrayList<String> mFilePaths) throws IOException {
        int size=mFilePaths.size();
        Log.d(TAG, "getOnlyEncryptedFiles: size is: "+size);
        for (int i = 0; i < size; i++) {
            String path=mFilePaths.get(i);
            Log.d(TAG, "getOnlyEncryptedFiles: file path is: "+path);
            File f=TasksFileUtils.getFile(path);
            if(f.isDirectory()){
                File[] fs=f.listFiles();
                ArrayList<String> tmp1=new ArrayList<>();
                for (File fin:
                    fs ) {
                    tmp1.add(fin.getAbsolutePath());
                }
                getOnlyEncryptedFiles(tmp1);
            }
                if(FileUtils.isEncryptedFile(path)){
                tmp.add(path);
            }
        }
        if(tmp.size()<1){
            //throw new IllegalArgumentException("No encrypted files found");
        }
        return tmp;
    }


    private void decryptFile(File f) throws Exception{
        Log.d(TAG, "decryptFile: task is running");
        if(!isCancelled()) {
            if (f.isDirectory()) {
                for (File tmp : f.listFiles()) {
                    decryptFile(tmp);
                }
            } else {
                File out = new File(rootPath+"/", f.getName().substring(0, f.getName().lastIndexOf('.')));
                if (out.exists()) {
                    if(!out.delete()){
                        throw new IOException("Error in deleting already present file");
                    }
                }

                publishProgress(f.getName(), "" +
                        ((FileUtils.getReadableSize((f.length())))));
                mCreatedFiles.add(out);
                if(!EncryptionWrapper.decryptFile(f, out, mPubKey, getSecretKey(), mKeyPass)){
                    if(out.delete()){
                        throw new Exception("Error in decrypting file");
                    }
                }else{
                    FileUtils.notifyChange(mContext,out.getAbsolutePath());
                }
            }
            if(isRootPath){
                f.delete();
            }
        }

    }

    private InputStream getSecretKey() {
        SQLiteDatabase.loadLibs(mContext);
        try {
            DatabaseHandler handler=new DatabaseHandler(mContext,mDbPassword,true);
            mSecKey= new BufferedInputStream(new ByteArrayInputStream(handler.getSecretKeyFromDb(mUsername)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert mSecKey!=null;
        return mSecKey;
    }

    @Override
    protected void onPostExecute(String s) {
        if (singleFileMode) {
            if (s.equals(DECRYPTION_SUCCESS_MESSAGE)) {
                singleModeDialog.dismiss();
                Log.d(TAG, "onPostExecute: destination filename is: " + destFilename);
                //open file
                UiUtils.openFile(destFilename, mContext, mAdapter);
            } else {
                singleModeDialog.dismiss();
                Toast.makeText(mContext,
                        s,
                        Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            mProgressDialog.dismiss("Decryption completed");
            SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        if (s.equals(DECRYPTION_SUCCESS_MESSAGE)) {
            Toast.makeText(mContext,"Decrypted files can be found in your home folder.",Toast.LENGTH_LONG).show();
            UiUtils.reloadData(mAdapter);
        } else {
            Toast.makeText(mContext,
                    s,
                    Toast.LENGTH_LONG)
                    .show();
            SharedData.KEY_PASSWORD = null;
        }
    }

    }


    @Override
    protected void onProgressUpdate(String... values) {
        if(singleFileMode){
            return;
        }
        mProgressDialog.setmProgressTextViewText(values[0]);
    }

    @Override
    protected void onPreExecute() {
        if(singleFileMode){
            singleModeDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            singleModeDialog.setTitle("Decrypting file");
            singleModeDialog.setMessage("Please wait while I finish decrypting file");
            singleModeDialog.setIndeterminate(true);
            singleModeDialog.show();
            return;
        }
        mProgressDialog.show();
    }

    @Override
    protected void onCancelled() {

        for (File f : mCreatedFiles) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        UiUtils.reloadData(
                mAdapter
        );

        Toast.makeText(
                mContext,
                "Operation canceled",
                Toast.LENGTH_SHORT
        ).show();

        mProgressDialog.dismiss("Canceled");
        super.onCancelled();

    }



private void perfromRootOperation() throws IOException{
    ArrayList<String> tmp=new ArrayList<>();
    for (int i = 0; i < mFilePaths.size(); i++) {
        String path=mFilePaths.get(i);
        path=path.replace(mCurrentPath,"");
        RootUtils.copyFile(path,mRootHandlingPath);
        path=mRootHandlingPath+path+"/";
        tmp.add(path);
    }
    mFilePaths=tmp;
}
    private void performNormalFormDecryption() throws Exception{
        tmp.clear();
        //refactor list to hold only encrypted files
        if(isRootPath){
            perfromRootOperation();
        }
        mFilePaths=getOnlyEncryptedFiles(mFilePaths);
        for (String s : mFilePaths) {
            if(!isCancelled()) {
                Log.d(TAG, "doInBackground: +" + mFilePaths.size());
                File f = TasksFileUtils.getFile(s);
                decryptFile(f);
            }
        }
    }
}
