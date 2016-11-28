package com.cryptopaths.cryptofm;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptopaths.cryptofm.encryption.EncryptionManagement;

import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class IntermediateActivity extends AppCompatActivity {
    private InputStream             mSecKeyFile;
    private File                    mPubKeyFile;
    private EncryptionManagement    mEncryptionManagement;
    private ProgressBar             mProgressBar;
    private TextView                mTextView;

    private static final String TAG=    "InterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);
        //get the key from intent and convert it to file
        byte[] key = getIntent().getExtras().getByteArray("key");
        assert key!=null;
        try {
            mSecKeyFile = PGPUtil.getDecoderStream(new ByteArrayInputStream(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPubKeyFile=new File(getFilesDir(),"pub.asc");
        mEncryptionManagement=new EncryptionManagement();
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mTextView=(TextView)findViewById(R.id.progress_text);
        //lets encrypt
        new EncryptTask().execute();
    }


    private class EncryptTask extends AsyncTask<Void,String,String>{

        @Override
        protected String doInBackground(Void... voids) {
            if(!isExternalStorageWritable()){
                return "Cannot write files";
            }
            File dir=new File("/sdcard/Download/");
            File[] files=dir.listFiles();
            for (File file:
                 files) {
                //if file is already encrypted
                if(file.getName().contains("pgp")){
                    continue;
                }
                File outputFile=new File("/sdcard/Download/"+file.getName()+".pgp");
                try{
                    if(outputFile.createNewFile()){
                        Log.d(TAG,"created file to encrypt into");
                    }
                    Log.d(TAG,"encrypting file: "+file.getName());
                    publishProgress(file.getName());
                    mEncryptionManagement.encryptFile(outputFile,file,mPubKeyFile);
                    //after encryption delete original file
                    if(file.delete()){
                        Log.d(TAG," deleted file after encryption");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return "Error";
                }

            }
            return "Fucked the whole universe";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("test","Encrypting file");

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mTextView.setText(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(IntermediateActivity.this, s, Toast.LENGTH_LONG).show();
        }
        /* Checks if external storage is available for read and write */
        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }
    }
}
