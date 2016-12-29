package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.cryptopaths.cryptofm.encryption.EncryptionManagement;
import com.cryptopaths.cryptofm.filemanager.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.UiUtils;
import com.cryptopaths.cryptofm.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by home on 12/29/16.
 * encrypt file task
 */

public class EncryptTask extends AsyncTask<Void,String,String> {
    private ArrayList<String>       mFilePaths;
    private FileListAdapter         mAdapter;
    private ProgressDialog          mProgressDialog;
    private Context                 mContext;
    private EncryptionManagement    encryptionManagement;
    private File                    pubKeyFile;

    private static final String TAG = "encrypt";

    public EncryptTask(Context context,FileListAdapter adapter,ArrayList<String> filePaths){
        this.mAdapter               = adapter;
        this.mContext               = context;
        this.mFilePaths             = filePaths;
        this.mProgressDialog        = new ProgressDialog(mContext);
        this.encryptionManagement   = new EncryptionManagement();
        this.pubKeyFile             = new File(mContext.getFilesDir(),"pub.asc");
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            for (String path : mFilePaths) {
                File f = TasksFileUtils.getFile(path);
                encryptFile(f);
            }
            return "successfully encrypted file(s)";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error in encrypting file." + ex.getMessage();
        }
    }
    private void encryptFile(File f) throws Exception{
        if(f.isDirectory()){
            for (File tmp: f.listFiles()) {
                encryptFile(tmp);
            }
        }else {
            File out = new File(f.getAbsolutePath()+".pgp");
            if(out.createNewFile()){
                Log.d(TAG, "encryptFile: created file to encrypt into");
            }
            publishProgress(f.getName(),""+
                    ((FileUtils.getReadableSize((f.length())))));

            encryptionManagement.encryptFile(out,f,pubKeyFile);

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
                Toast.LENGTH_SHORT
        ).show();
        deleteAlertDialog();

    }
    private void deleteAlertDialog(){
        //ask the user if he/she wants to delete the unencrypted version of file
        AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
        dialog.setMessage("Do you want to delete the unencrypted version of folders?");
        dialog.setTitle("Delete unencrypted files");
        dialog.setPositiveButton("Yes, Sure!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DeleteTask(mContext,mAdapter,mFilePaths).execute();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UiUtils.reloadData(
                        mContext,
                        mAdapter
                );
            }
        });
        dialog.show();
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setTitle("Encrypting data");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }
}
