package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.EncryptionManagement;
import com.cryptopaths.cryptofm.filemanager.FileListAdapter;

import org.spongycastle.openpgp.PGPUtil;

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
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private EncryptionManagement encryptionManagement;
    private InputStream mSecKey;
    private String mDbPassword;
    private String mUsername;
    private String mFileName=null;
    private File mPubKey;
    private String mKeyPass;
    public DecryptTask(Context context,FileListAdapter adapter,
                       ArrayList<String> filePaths,
                       String DbPass,String mUsername,String keypass){
        this.mContext               = context;
        this.mAdapter               = adapter;
        this.mFilePaths             = filePaths;
        this.mUsername              = mUsername;
        this.mKeyPass               = keypass;
        this.mSecKey                = getSecretKey();
        this.mDbPassword            = DbPass;
        this.mProgressDialog        = new ProgressDialog(mContext);
        this.encryptionManagement   = new EncryptionManagement();
        this.mPubKey                = new File(mContext.getFilesDir(),"pub.asc");
    }
    private DecryptTask(Context context, FileListAdapter adapter,String DbPass,String mUsername,String f){
        this.mContext=context;
        this.mAdapter=adapter;
        this.mFileName=f;
        this.mDbPassword=DbPass;
        this.mUsername=mUsername;
        this.mProgressDialog=new ProgressDialog(mContext);
        this.encryptionManagement=new EncryptionManagement();
    }
    @Override
    protected String doInBackground(Void... voids) {
        try{
            if(mFileName==null){
                for (String s : mFilePaths) {
                    File f =TasksFileUtils.getFile(s);
                }
            }else{
                File in= TasksFileUtils.getFile(mFileName);
                File out= new File(in.getPath()+in.getName()+TasksFileUtils.getEncryptedFileExtension(mFileName));
                out.createNewFile();
                encryptionManagement.decryptFile(in,out,mPubKey,mSecKey,mKeyPass.toCharArray());
            }

        }catch (Exception ex){
            ex.printStackTrace();
            return "cannot decrypt file";
        }

        return "decryption successful";
    }

    private InputStream getSecretKey() {
        DatabaseHandler handler=DatabaseHandler.getInstance(mDbPassword,mContext,true);
        try {
            mSecKey= PGPUtil.getDecoderStream(new ByteArrayInputStream(handler.getSecretKeyFromDb(mUsername)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert mSecKey!=null;
        return mSecKey;
    }
}
