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

package com.osama.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import com.osama.cryptofm.filemanager.listview.FileListAdapter;
import com.osama.cryptofm.filemanager.utils.UiUtils;
import com.osama.cryptofm.utils.FileDocumentUtils;
import com.osama.cryptofm.utils.FileUtils;

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
            if(FileUtils.isDocumentFile(mFilePath)){
                 Log.d("niggers", "doInBackground: renaming document file");
                DocumentFile file= FileDocumentUtils.getDocumentFile(new File(mFilePath));
                assert file!=null;
                if(file.renameTo(mRenamed)){

                    return "successfully renamed";
                }else{
                    return "Cannot rename file";
                }
            }
            Log.d("niggers", "doInBackground: current path is: "+FileUtils.CURRENT_PATH);
            File file = TasksFileUtils.getFile(mFilePath);
            String filename=file.getPath();
            filename=filename.replace(file.getName(),"");
            File renamedFile = new File(filename+mRenamed);
            if(file.renameTo(renamedFile)){
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
                mContext,
                mAdapter
        );
    }
}
