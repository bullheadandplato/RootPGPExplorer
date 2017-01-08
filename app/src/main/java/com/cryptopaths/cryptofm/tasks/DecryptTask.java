package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.EncryptionWrapper;
import com.cryptopaths.cryptofm.filemanager.FileBrowserActivity;
import com.cryptopaths.cryptofm.filemanager.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.UiUtils;
import com.cryptopaths.cryptofm.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by tripleheader on 1/2/17.
 * file decryption task
 */

public class DecryptTask extends AsyncTask<Void,String,String> {
    private ArrayList<String> mFilePaths;
    private FileListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private InputStream mSecKey;
    private String mDbPassword;
    private String mUsername;
    private String mFileName=null;
    private File mPubKey;
    private char[] mKeyPass;
    private String rootPath;
    private static final String TAG="decrypt";

    public DecryptTask(Context context,FileListAdapter adapter,
                       ArrayList<String> filePaths,
                       String DbPass,String mUsername,String keypass){
        this.mContext               = context;
        this.mAdapter               = adapter;
        this.mFilePaths             = filePaths;
        this.mUsername              = mUsername;
        this.mKeyPass               = keypass.toCharArray();
        this.mDbPassword            = DbPass;
        this.mSecKey                = getSecretKey();
        this.mProgressDialog        = new ProgressDialog(mContext);
        this.mPubKey                = new File(mContext.getFilesDir(),"pub.asc");


    }
    private DecryptTask(Context context, FileListAdapter adapter,String DbPass,String mUsername,String f){
        this.mContext               = context;
        this.mAdapter               = adapter;
        this.mFileName              = f;
        this.mDbPassword            = DbPass;
        this.mUsername              = mUsername;
        this.mSecKey                =  getSecretKey();
        this.mProgressDialog        = new ProgressDialog(mContext);
    }
    @Override
    protected String doInBackground(Void... voids) {
        try{
            File root= new File(Environment.getExternalStorageDirectory(),"decrypted");
            if(!root.exists()){
                if(!root.mkdir()){
                    return "cannot decrypt file";
                }
            }
            rootPath=root.getPath();
            if(mFileName==null){
                for (String s : mFilePaths) {
                    Log.d(TAG, "doInBackground: +"+mFilePaths.size());
                    File f =TasksFileUtils.getFile(s);
                    decryptFile(f);
                }
            }else{
                File in= TasksFileUtils.getFile(mFileName);
                File out= new File(root.getPath()+in.getName().substring(0,in.getName().lastIndexOf('.')));
                if(out.exists()){
                    throw new Exception("file already decrypted");
                }
                //out.createNewFile();
                mSecKey=getSecretKey();
                EncryptionWrapper.decryptFile(in,out,mPubKey,getSecretKey(),mKeyPass);
            }

        }catch (Exception ex){
            //let the activity know that password is incorrect and don't save it
            ((FileBrowserActivity)mContext).resetmKeyPass();
            ex.printStackTrace();
            return ex.getMessage();
        }

        return "decryption successful";
    }
    private void decryptFile(File f) throws Exception{
        if(f.isDirectory()){
            for (File tmp: f.listFiles()) {
                decryptFile(tmp);
            }
        }else {
            Log.d(TAG, "decryptFile: rootpath is : "+f.getPath());
            File out = new File(rootPath,f.getName().substring(0,f.getName().lastIndexOf('.')));
            if(out.exists()){
                throw new Exception("file already decrypted");
            }

            publishProgress(f.getName(), "" +
                    ((FileUtils.getReadableSize((f.length())))));

            EncryptionWrapper.decryptFile(f, out, mPubKey, getSecretKey(), mKeyPass);
        }

    }

    private InputStream getSecretKey() {
        DatabaseHandler handler=DatabaseHandler.getInstance(mDbPassword,mContext,true);
        try {
            mSecKey= new BufferedInputStream(new ByteArrayInputStream(handler.getSecretKeyFromDb(mUsername)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert mSecKey!=null;
        return mSecKey;
    }

    @Override
    protected void onPostExecute(String s) {
        mProgressDialog.dismiss();
        Toast.makeText(mContext,
                s,
                Toast.LENGTH_LONG)
                .show();
        UiUtils.reloadData(mContext,mAdapter);

    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setTitle("Decrypting data");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }
}
