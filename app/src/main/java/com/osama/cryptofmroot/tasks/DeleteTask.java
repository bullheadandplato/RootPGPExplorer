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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.osama.cryptofmroot.filemanager.listview.FileListAdapter;
import com.osama.cryptofmroot.filemanager.utils.UiUtils;
import com.osama.cryptofmroot.root.RootUtils;
import com.osama.cryptofmroot.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by home on 12/27/16.
 * task to delete file and folders
 */

public class DeleteTask extends AsyncTask<Void,String,String>{
    private ArrayList<String>   mFilePaths;
    private FileListAdapter     mAdapter;
    private ProgressDialog      mProgressDialog;
    private Context             mContext;

    private boolean             isRunningFromEncryption=false;
    
    private static final String TAG=DeleteTask.class.getName();

    public DeleteTask(Context context, FileListAdapter adpater, ArrayList<String> filePaths){
        mFilePaths      = filePaths;
        mAdapter        = adpater;
        mContext        = context;
        mProgressDialog = new ProgressDialog(context);
    }

    public void setRunningFromEncryption(boolean runningFromEncryption) {
        isRunningFromEncryption = runningFromEncryption;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            for (String f :
                    mFilePaths) {
                Log.d("delete","filepath: " +f);
                if(RootUtils.isRootPath(f)){
                    publishProgress(f);
                    RootUtils.deleteFile(f);
                }else {
                    File file = TasksFileUtils.getFile(f);
                    deleteFile(file);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "failed "+ ex.getMessage();
        }
        return "successfully deleted file(s)";

    }

    private void deleteFile(File f) throws IOException{
        Log.d(TAG, "deleteFile: Deleting normal file");
        publishProgress(f.getName());

        if (f.isDirectory() && f.listFiles().length > 0) {
            for (File tmp :
                    f.listFiles()) {
                deleteFile(tmp);
            }
        }
        if (!f.delete()) {
            throw new IOException("error in deleting file");
        }else{
            //reflect the change in file system
            FileUtils.removeMediaStore(mContext,f);

            FileUtils.notifyChange(mContext,f.getAbsolutePath());
        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        mProgressDialog.setMessage(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        mProgressDialog.dismiss();
        if(!isRunningFromEncryption) {
            Toast.makeText(
                    mContext,
                    s,
                    Toast.LENGTH_LONG
            ).show();
        }
        UiUtils.reloadData(
                mAdapter
                );

    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setTitle("Deleting file(s)");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }
}
