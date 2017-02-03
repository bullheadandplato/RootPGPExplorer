package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.utils.UiUtils;
import com.cryptopaths.cryptofm.utils.FileDocumentUtils;

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
    
    private static final String TAG=DeleteTask.class.getName();

    public DeleteTask(Context context, FileListAdapter adpater, ArrayList<String> filePaths){
        mFilePaths      = filePaths;
        mAdapter        = adpater;
        mContext        = context;
        mProgressDialog = new ProgressDialog(context);
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            for (String f :
                    mFilePaths) {
                Log.d("delete","filepath: " +f);

                File file = TasksFileUtils.getFile(f);
                  if(f.contains(SharedData.EXTERNAL_SDCARD_ROOT_PATH)){
                        //this means it is external storage file
                        DocumentFile docFile= FileDocumentUtils.getDocumentFile(file);
                        deleteDocFile(docFile);
                }      else{
                         deleteFile(file);
                  }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "failed "+ ex.getMessage();
        }
        return "successfully deleted file(s)";

    }

    private void deleteDocFile(DocumentFile docFile) throws IOException{
        Log.d(TAG, "deleteDocFile: Deleting document file");
        publishProgress(docFile.getName());
        if(docFile.isDirectory() && docFile.listFiles().length>0){
            for (DocumentFile d: docFile.listFiles()) {
                deleteDocFile(d);
            }
        }
        if(!docFile.delete()){
            throw new IOException("cannot delete files");
        }
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
            throw new IOException("error is deleting file");
        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        mProgressDialog.setMessage(values[0]);
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

    @Override
    protected void onPreExecute() {
        mProgressDialog.setTitle("Deleting file(s)");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }
}
