package com.cryptopaths.cryptofm.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.EncryptionWrapper;
import com.cryptopaths.cryptofm.filemanager.FileBrowserActivity;
import com.cryptopaths.cryptofm.filemanager.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptofm.filemanager.UiUtils;
import com.cryptopaths.cryptofm.utils.FileUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by tripleheader on 1/2/17.
 * file decryption task
 */

public class DecryptTask extends AsyncTask<Void,String,String> {
    private ArrayList<String> mFilePaths;
    private FileListAdapter mAdapter;
    private MyProgressDialog mProgressDialog;
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
        this.mProgressDialog        = new MyProgressDialog(mContext,"Decrypting",this);
        this.mPubKey                = new File(mContext.getFilesDir(),"pub.asc");


    }
    private DecryptTask(Context context, FileListAdapter adapter,String DbPass,String mUsername,String f){
        this.mContext               = context;
        this.mAdapter               = adapter;
        this.mFileName              = f;
        this.mDbPassword            = DbPass;
        this.mUsername              = mUsername;
        this.mSecKey                = getSecretKey();
        this.mProgressDialog        = new MyProgressDialog(mContext,"Decrypting",this);
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
                //refactor list to hold only encrypted files
                mFilePaths=getOnlyEncryptedFiles(mFilePaths);
                for (String s : mFilePaths) {
                    if(!isCancelled()) {
                        Log.d(TAG, "doInBackground: +" + mFilePaths.size());
                        File f = TasksFileUtils.getFile(s);
                        decryptFile(f);
                    }

                }
            }else{
                File in= TasksFileUtils.getFile(mFileName);
                File out= new File(root.getPath()+in.getName().substring(0,in.getName().lastIndexOf('.')));
                if(out.exists()){
                    throw new Exception("file already decrypted");
                }
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
    ArrayList<String> tmp=new ArrayList<>();
    private ArrayList<String> getOnlyEncryptedFiles(ArrayList<String> mFilePaths) throws IOException {
        int size=mFilePaths.size();
        for (int i = 0; i < size; i++) {
            Log.d("enclist", "getOnlyEncryptedFiles: file path is: "+mFilePaths.get(i));
            File f=TasksFileUtils.getFile(mFilePaths.get(i));
            if(f.isDirectory()){
                File[] fs=f.listFiles();
                ArrayList<String> tmp1=new ArrayList<>();
                for (File fin:
                    fs ) {
                    tmp1.add(fin.getAbsolutePath());
                }
                getOnlyEncryptedFiles(tmp1);
            }
                if(FileUtils.isEncryptedFile(mFilePaths.get(i))){
                tmp.add(mFilePaths.get(i));
            }
        }
        if(tmp.size()<1){
            throw new IllegalArgumentException("No encrypted files found");
        }
        return tmp;
    }

    private void decryptFile(File f) throws Exception{
        Log.d(TAG, "decryptFile: task is running");
        if(!isCancelled()) {
            if (f.isDirectory()) {
                for (File tmp : f.listFiles()) {
                    decryptFile(tmp);
                }
            } else {
                Log.d(TAG, "decryptFile: rootpath is : " + f.getPath());
                File out = new File(rootPath, f.getName().substring(0, f.getName().lastIndexOf('.')));
                if (out.exists()) {
                    out.delete();
                }

                publishProgress(f.getName(), "" +
                        ((FileUtils.getReadableSize((f.length())))));

                EncryptionWrapper.decryptFile(f, out, mPubKey, getSecretKey(), mKeyPass);
            }
        }

    }

    private InputStream getSecretKey() {
        SQLiteDatabase.loadLibs(mContext);
        DatabaseHandler handler=new DatabaseHandler(mContext,mDbPassword,true);
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
        mProgressDialog.dismiss("Decryption completed");
        SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        Toast.makeText(mContext,
                s,
                Toast.LENGTH_LONG)
                .show();
        UiUtils.reloadData(mContext,mAdapter);

    }

    @Override
    protected void onProgressUpdate(String... values) {
        mProgressDialog.setmProgressTextViewText(values[0]);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }

    @Override
    protected void onCancelled() {
        SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        mProgressDialog.dismiss("Canceled");
        super.onCancelled();

    }
}
