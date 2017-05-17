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
import android.widget.Toast;

import com.slownet5.pgprootexplorer.filemanager.listview.FileListAdapter;
import com.slownet5.pgprootexplorer.filemanager.utils.UiUtils;
import com.slownet5.pgprootexplorer.root.RootUtils;
import com.slownet5.pgprootexplorer.utils.FileUtils;

import java.io.File;

/**
 * Created by home on 12/28/16.
 * file rename task
 */

public class RenameTask extends AsyncTask<Void,Void,String> {
    private Context         mContext;
    private String          mFilePath;
    private ProgressDialog  mProgressDialog;
    private String          mRenamed;
    private FileListAdapter mAdapter;

    public RenameTask(Context con,FileListAdapter adapter,String filePath,String rename){
        this.mContext   = con;
        this.mFilePath  = filePath;
        this.mRenamed   = rename;
        this.mAdapter   = adapter;
        mProgressDialog = new ProgressDialog(mContext);
    }
    @Override
    protected String doInBackground(Void... voids) {
        try{

            if(RootUtils.isRootPath(mFilePath)){
                RootUtils.renameFile(mFilePath,mFilePath.substring(0,mFilePath.lastIndexOf("/"))+mRenamed);
                return "Successfully renamed.";
            }
            File file = TasksFileUtils.getFile(mFilePath);
            String filename=file.getPath();
            filename=filename.replace(file.getName(),"");
            File renamedFile = new File(filename+mRenamed);
            if(file.renameTo(renamedFile)){
                FileUtils.notifyChange(mContext,renamedFile.getAbsolutePath());
                return "successfully renamed.";
            }
        }catch (Exception ex){
            ex.getMessage();
            return "cannot rename file.";
        }
        return "cannot rename file.";
    }

    @Override
    protected void onPostExecute(String s) {

        mProgressDialog.dismiss();
        Toast.makeText(
                mContext,
                s,
                Toast.LENGTH_LONG
        ).show();
        UiUtils.reloadData(
                mAdapter
        );
    }
}
