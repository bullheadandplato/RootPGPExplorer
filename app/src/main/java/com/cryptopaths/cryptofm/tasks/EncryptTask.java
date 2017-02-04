package com.cryptopaths.cryptofm.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.cryptopaths.cryptofm.encryption.EncryptionSmallFileProcessor;
import com.cryptopaths.cryptofm.encryption.EncryptionWrapper;
import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.utils.UiUtils;
import com.cryptopaths.cryptofm.utils.FileDocumentUtils;
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
    private MyProgressDialog        mProgressDialog;
    private Context                 mContext;
    private File                    pubKeyFile;

    private ArrayList<File>         mCreatedFiles=new ArrayList<>();
    private ArrayList<String>       mUnencryptedFiles=new ArrayList<>();

    private static final String TAG                         = EncryptTask.class.getName();
    private static final String ENCRYPTION_SUCCESS_MESSAGE  = "Successfully encrypted files";

    public EncryptTask(Context context,FileListAdapter adapter,ArrayList<String> filePaths){
        this.mAdapter           = adapter;
        this.mContext           = context;
        this.mFilePaths         = filePaths;
        this.mProgressDialog    = new MyProgressDialog(context,"Encrypting",this);
        this.pubKeyFile         = new File(mContext.getFilesDir(),"pub.asc");
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            for (String path : mFilePaths) {
                if(!isCancelled()){
                    File f = TasksFileUtils.getFile(path);
                    encryptFile(f);
                }

            }
            return ENCRYPTION_SUCCESS_MESSAGE;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error in encrypting file." + ex.getMessage();
        }
    }

    private void encryptFile(File f) throws Exception{
        if(!isCancelled()){
            if(f.isDirectory()){
                for (File tmp: f.listFiles()) {
                    encryptFile(tmp);
                    mFilePaths.remove(f.getAbsolutePath());
                }
            }else {
                File out = new File(f.getAbsolutePath()+".pgp");
                if(out.createNewFile()){
                    Log.d(TAG, "encryptFile: created file to encrypt into");
                }
                publishProgress(f.getName(),""+
                        ((FileUtils.getReadableSize((f.length())))));
                mCreatedFiles.add(out);
                mUnencryptedFiles.add(f.getAbsolutePath());
                EncryptionWrapper.encryptFile(f,out,pubKeyFile,true);

            }
        }


    }

    @Override
    protected void onProgressUpdate(String... values) {
        mProgressDialog.setmProgressTextViewText(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        if(s.equals(ENCRYPTION_SUCCESS_MESSAGE)){
            deleteAlertDialog();
        }
        mProgressDialog.dismiss(s);
        Toast.makeText(
                mContext,
                s,
                Toast.LENGTH_SHORT
        ).show();
        SharedData.CURRENT_RUNNING_OPERATIONS.clear();

    }

    private void deleteAlertDialog(){
        //ask the user if he/she wants to delete the unencrypted version of file
        AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
        dialog.setMessage("Do you want to delete the unencrypted version of folders?");
        dialog.setTitle("Delete unencrypted files");
        dialog.setPositiveButton("Yes, Sure!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                new DeleteTask(mContext,mAdapter,mUnencryptedFiles).execute();
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
        mProgressDialog.show();
    }

    @Override
    protected void onCancelled() {
        for (File f : mCreatedFiles) {
            if(SharedData.EXTERNAL_SDCARD_ROOT_PATH!=null &&
                    f.getAbsolutePath().contains(SharedData.EXTERNAL_SDCARD_ROOT_PATH)){
                FileDocumentUtils.getDocumentFile(f).delete();
            }else{
                f.delete();
            }
        }

        Toast.makeText(
                mContext,
                "Encryption canceled",
                Toast.LENGTH_SHORT
        ).show();
        SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        UiUtils.reloadData(
                mContext,
                mAdapter
        );
        Log.d("cancel","yes task is canceled");
        super.onCancelled();
    }
}
