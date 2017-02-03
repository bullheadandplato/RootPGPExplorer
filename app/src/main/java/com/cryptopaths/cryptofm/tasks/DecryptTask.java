package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.EncryptionWrapper;
import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.utils.UiUtils;
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
    private ArrayList<String>   mFilePaths;
    private FileListAdapter     mAdapter;
    private MyProgressDialog    mProgressDialog;
    private Context             mContext;
    private InputStream         mSecKey;
    private String              mDbPassword;
    private String              mUsername;
    private String              mFileName=null;
    private File                mPubKey;
    private char[]              mKeyPass;
    private String              rootPath;
    private static final String TAG="decrypt";
    private ArrayList<File>     mCreatedFiles=new ArrayList<>();
    private boolean             singleFileMode=false;
    private ProgressDialog      singleModeDialog;
    private String              destFilename;
    private static final String DECRYPTION_SUCCESS_MESSAGE="Decryption successful";

    public DecryptTask(Context context,FileListAdapter adapter,
                       ArrayList<String> filePaths,
                       String DbPass,String mUsername,String keypass){
        this.mContext           = context;
        this.mAdapter           = adapter;
        this.mFilePaths         = filePaths;
        this.mUsername          = mUsername;
        this.mKeyPass           = keypass.toCharArray();
        this.mDbPassword        = DbPass;
        this.mSecKey            = getSecretKey();
        this.mProgressDialog    = new MyProgressDialog(mContext,"Decrypting",this);
        this.mPubKey            = new File(mContext.getFilesDir(),"pub.asc");


    }
    public DecryptTask(Context context, FileListAdapter adapter,String DbPass,String mUsername,String filename,String keypass){
        this.mContext           = context;
        this.mAdapter           = adapter;
        this.mFileName          = filename;
        this.mDbPassword        = DbPass;
        this.mKeyPass           = keypass.toCharArray();
        this.mUsername          = mUsername;
        this.mSecKey            = getSecretKey();
        this.singleModeDialog   = new ProgressDialog(mContext);
        this.singleFileMode     = true;

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
                File out= new File(root.getPath()+"/"+in.getName().substring(0,in.getName().lastIndexOf('.')));
                destFilename=out.getAbsolutePath();
                if(out.exists()){
                    throw new Exception("file already decrypted");
                }
                mSecKey=getSecretKey();
                EncryptionWrapper.decryptFile(in,out,mPubKey,getSecretKey(),mKeyPass);
            }

        }catch (Exception ex){
            //let the activity know that password is incorrect and don't save it
            SharedData.KEY_PASSWORD=null;
            ex.printStackTrace();
            return ex.getMessage();
        }

        return DECRYPTION_SUCCESS_MESSAGE;
    }
    private ArrayList<String> tmp=new ArrayList<>();
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
                File out = new File(rootPath+"/", f.getName().substring(0, f.getName().lastIndexOf('.')));
                if (out.exists()) {
                    if(!out.delete()){
                        throw new IOException("Error in deleting already present file");
                    }
                }

                publishProgress(f.getName(), "" +
                        ((FileUtils.getReadableSize((f.length())))));
                mCreatedFiles.add(out);
                if(!EncryptionWrapper.decryptFile(f, out, mPubKey, getSecretKey(), mKeyPass)){
                    if(out.delete()){
                        throw new Exception("Error in decrypting file");
                    }
                }
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
        if (singleFileMode && s.equals(DECRYPTION_SUCCESS_MESSAGE)) {
            singleModeDialog.dismiss();
            Log.d(TAG, "onPostExecute: destination filename is: " + destFilename);
            //open file
            String mimeType =
                    MimeTypeMap.getSingleton().
                            getMimeTypeFromExtension(
                                    FileUtils.getExtension(destFilename
                                    )
                            );

            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = null;
                try {
                    uri = FileProvider.getUriForFile(
                            mContext,
                            mContext.getApplicationContext().getPackageName() + ".provider",
                            TasksFileUtils.getFile(destFilename)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.setDataAndType(uri, mimeType);
            } else {
                try {
                    intent.setDataAndType(Uri.fromFile(TasksFileUtils.getFile(destFilename)), mimeType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            intent.setAction(Intent.ACTION_VIEW);
            Intent x = Intent.createChooser(intent, "Open with: ");
            mContext.startActivity(x);

        } else {
            mProgressDialog.dismiss("Decryption completed");
            SharedData.CURRENT_RUNNING_OPERATIONS.clear();
            Toast.makeText(mContext,
                    s,
                    Toast.LENGTH_LONG)
                    .show();
            UiUtils.reloadData(mContext, mAdapter);
        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        if(singleFileMode){
            return;
        }
        mProgressDialog.setmProgressTextViewText(values[0]);
    }

    @Override
    protected void onPreExecute() {
        if(singleFileMode){
            singleModeDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            singleModeDialog.setTitle("Decrypting file");
            singleModeDialog.setMessage("Please wait while I finish decrypting file");
            singleModeDialog.setIndeterminate(true);
            singleModeDialog.show();
            return;
        }
        mProgressDialog.show();
    }

    @Override
    protected void onCancelled() {
        SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        UiUtils.reloadData(
                mContext,
                mAdapter
        );
        for (File f:
                mCreatedFiles) {
            f.delete();
        }
        Toast.makeText(
                mContext,
                "Encryption canceled",
                Toast.LENGTH_SHORT
        ).show();
        mProgressDialog.dismiss("Canceled");
        super.onCancelled();

    }
}
