package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.cryptopaths.cryptofm.filemanager.FileBrowserActivity;
import com.cryptopaths.cryptofm.filemanager.FileFillerWrapper;
import com.cryptopaths.cryptofm.filemanager.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.UiUtils;

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
    private RecyclerView        mRecycle;

    public DeleteTask(Context context, FileListAdapter adpater, RecyclerView rec, ArrayList<String> filePaths){
        mFilePaths      = filePaths;
        mAdapter        = adpater;
        mContext        = context;
        mRecycle        = rec;
        mProgressDialog = new ProgressDialog(context);
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            for (String f :
                    mFilePaths) {
                Log.d("delete","filepath: " +f);
                File file = TasksFileUtils.getFile(f);
                deleteFile(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "failed "+ ex.getMessage();
        }
        return "success deleted file(s)";

    }
    private void deleteFile(File f) throws IOException{
        if(f.isDirectory() && f.listFiles().length>0){
            for (File tmp:
                 f.listFiles()) {
                deleteFile(tmp);
            }
        }          if (!f.delete()) {
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
