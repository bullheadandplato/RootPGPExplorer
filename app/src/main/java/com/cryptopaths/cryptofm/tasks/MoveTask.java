package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by tripleheader on 12/26/16.
 * File moving task
 */

public class MoveTask extends AsyncTask<String,String,String> {
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private HashMap<String,String> mFiles;
    public MoveTask(Context context,HashMap<String,String> files){
        this.mContext = context;
        mProgressDialog=new ProgressDialog(mContext);
        this.mFiles=files;
    }
    @Override
    protected String doInBackground(String... strings) {
        for (HashMap.Entry<String,String> entry:
             mFiles.entrySet()) {
            String sourcePath       = entry.getKey();
            String destinationPath  = entry.getValue();
            final int toAdd=2048;
            try{
                publishProgress(sourcePath);
                File sourceFile=TasksFileUtils.getFile(sourcePath);
                File destinationFile=TasksFileUtils.getFile(destinationPath);
                InputStream in=new BufferedInputStream(new FileInputStream(sourceFile));
                OutputStream out=new BufferedOutputStream(new FileOutputStream(destinationFile));
                byte[] data=new byte[2048];
                int start=0;
                for (int i = 0; i < sourceFile.length(); i+=toAdd) {
                    if(sourceFile.length()<start+toAdd){
                        out.write(in.read());
                        break;
                    }else{
                        in.read(data,start,toAdd);
                        out.write(data);
                        start+=toAdd;
                    }
                }
                //delete the input file
                in.close();
                out.close();
                if(sourceFile.delete()){
                    //do nothing
                }


            }catch (Exception ex){
                ex.printStackTrace();
            }
            return "failed";
        }
        return "success";
    }

    @Override
    protected void onProgressUpdate(String... values) {

        mProgressDialog.setMessage("Moving: "+values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mProgressDialog.dismiss();
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
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }
}
