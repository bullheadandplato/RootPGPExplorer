package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by tripleheader on 12/26/16.
 * File moving task
 */

public class MoveTask extends AsyncTask<String,String,String> {
    private Context             mContext;
    private static final String TAG=MoveTask.class.getName();
    private MyProgressDialog      mProgressDialog;
    private ArrayList<String>   mFiles;
    private String              mDestinationFolder;
    private boolean             isNextFile=true;

    public MoveTask(Context context,ArrayList<String> files,String destination){
        this.mContext           = context;
        mProgressDialog         = new MyProgressDialog(mContext,"Copying",this);
        this.mFiles             = files;
        this.mDestinationFolder = destination;
    }
    @Override
    protected String doInBackground(String... strings) {
        for (String source : mFiles) {
            try{
                move(TasksFileUtils.getFile(source));
            }catch (Exception ex){
                ex.printStackTrace();
                return "failed";

            }
        }
        return "success";
    }
    private void move(File f) throws Exception{
        if(f.isDirectory()){
            Log.d(TAG, "move: File is a directory");
            for (File file:
                f.listFiles() ) {
                move(file);
            }
        }else{
            Log.d(TAG, "move: Moving file: "+f.getName());
            Log.d(TAG, "move: Destination folder is: "+mDestinationFolder);
            isNextFile=true;
            publishProgress(f.getName());
            mProgressDialog.setProgress(0);
            String destinationPath = mDestinationFolder+f.getName();
            File destinationFile   = TasksFileUtils.getFile(destinationPath);
            InputStream in         = new BufferedInputStream(new FileInputStream(f));
            OutputStream out       = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] data            = new byte[2048];
            int start              = 0;
            long totalFileLength   = f.length();
            long   readData        = 0;
            isNextFile=false;
            while ((start = in.read(data)) > 0){
                out.write(data, 0, start);
                readData+=start;
                publishProgress(""+totalFileLength/readData);
            }
            //delete the input file
            in.close();
            out.close();
            if(!f.delete()){
                throw new IOException("cannot move files");
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if(isNextFile){
            mProgressDialog.setMessage("Moving: "+values);
        }else{
            mProgressDialog.setProgress(Integer.valueOf(values[0]));
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mProgressDialog.dismiss(s);
        Toast.makeText(
                mContext,
                s,
                Toast.LENGTH_SHORT
        ).show();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Moving file");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }
}
