package com.cryptopaths.cryptofm.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.utils.UiUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
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
    private MyProgressDialog    mProgressDialog;
    private ArrayList<String>   mFiles;
    private String              mDestinationFolder;
    private boolean             isNextFile=true;
    private String              originalDestination;
    private FileListAdapter     mAdapter;

    public MoveTask(Context context,ArrayList<String> files,String destination,FileListAdapter mAdapter){
        this.mContext               = context;
        mProgressDialog             = new MyProgressDialog(mContext,"Copying",this);
        this.mFiles                 = files;
        this.mDestinationFolder     = destination;
        this.originalDestination    =destination;
        this.mAdapter=mAdapter;
    }
    @Override
    protected String doInBackground(String... strings) {
        String sourcePath=null;
      if(mDestinationFolder.contains(mFiles.get(0))){
            sourcePath=null;
            //check if user pasting in the folder it has selected to copy or more haha
            for (String s:
                 mFiles) {
                if(mDestinationFolder.equals(s+"/")){
                    sourcePath=s;
                    break;
                }
            }
        }
        //remove the destination folder from selected list if its there
        if(sourcePath!=null){
            mFiles.remove(sourcePath);
        }
        for (String source : mFiles) {
            try{
                //check if destination folder is same as source

                mDestinationFolder=originalDestination;
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
            //create folder in the destination
            mDestinationFolder=mDestinationFolder+f.getName()+"/";
            File tmp=new File(mDestinationFolder);
            if(!tmp.exists()){
                tmp.mkdir();
            }else{
                return;
            }
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
            publishProgress(""+0);
            //check if file already exits
            File destinationFile   =new File(mDestinationFolder+f.getName());
            if(destinationFile.exists()){
                return;
            }
            InputStream in         = new BufferedInputStream(new FileInputStream(f));
            OutputStream out       = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] data            = new byte[2048];
            int start              = 0;
            long totalFileLength   = f.length();
            long   readData        = 0;
            isNextFile=false;
            double progress;

            while ((start = in.read(data)) > 0){
                out.write(data, 0, start);
                readData+=start;
                progress=(double) readData/(double) totalFileLength;
                if(mProgressDialog.isInNotifyMode()){
                    mProgressDialog.setProgress((int)(progress*100));
                }else {
                    publishProgress(""+(int)(progress*100));
                }

            }
            out.flush();
            
            in.close();
            out.close();

        }

        //delete the input file
        //if copying then don't
        if(SharedData.IS_COPYING_NOT_MOVING){
           return;
        }
        if(!f.delete()){
            throw new IOException("cannot move files");
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        try{
            int progress=Integer.valueOf(values[0]);
            mProgressDialog.setProgress(progress);
        }catch (NumberFormatException e){
            mProgressDialog.setMessage(values[0]);
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
        UiUtils.reloadData(mContext,mAdapter);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Moving file");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }
}
