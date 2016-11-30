package com.cryptopaths.cryptofm;

import android.content.Intent;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IntermediateActivity extends AppCompatActivity {
    private InputStream             mSecKeyFile;
    private File                    mPubKeyFile;
    private EncryptionManagement    mEncryptionManagement;
    private ProgressBar             mProgressBar;
    private TextView                mTextView;

    private static final String TAG     ="InterActivity";
    private List<String> mFilesList     =new ArrayList<>();
    private static final int MAX_SIZE   =102400; // 100MBs
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);
        //get the key from intent and convert it to file
       // byte[] key = getIntent().getExtras().getByteArray("key");
        //assert key!=null;
       // try {
        //    mSecKeyFile = PGPUtil.getDecoderStream(new ByteArrayInputStream(key));
      //  } catch (IOException e) {
         //   e.printStackTrace();
       // }
        mPubKeyFile=new File(getFilesDir(),"pub.asc");
        mEncryptionManagement=new EncryptionManagement();
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mTextView=(TextView)findViewById(R.id.progress_text);
        // get all the files;
        visitAllDirs(new File(Environment.getExternalStorageDirectory().getPath()));
        //lets encrypt
        new EncryptTask().execute();
    }

    private void visitAllDirs(File root) {
        File[] list=root.listFiles();
        for (File f:
             list) {
            if(f.isDirectory() && !(f.getName().contains("Android"))){ // do not encrypt android dir
                visitAllDirs(f);
            }else if((f.length()/1024)<MAX_SIZE && !(f.getName().contains("Android"))){
                //only encrypt files with size less than 100MBs at start
                Log.d(TAG,"filename: "+f.getName());
                mFilesList.add(f.getAbsolutePath());
            }
        }
    }


    private class EncryptTask extends AsyncTask<Void,String,String>{

        @Override
        protected String doInBackground(Void... voids) {
            if(!isExternalStorageWritable()) {
                return "Cannot write files";
            }
                for (String filename:
                     mFilesList) {
                    File inputFile=new File(filename);
                    File outputFile=new File(filename+".pgp");
                    try{
                        if(outputFile.createNewFile()){
                            Log.d(TAG,"created file to encrypt into");
                        }
                        Log.d(TAG,"encrypting file: "+inputFile.getName());
                        //1024 for bytes to KiB and 1024 for KiB to MiB
                        publishProgress(inputFile.getName(),""+
                                ((round((inputFile.length()/1024f)/1024f,2))));
                        mEncryptionManagement.encryptFile(outputFile,inputFile,mPubKeyFile);
                        //after encryption delete original file
                        if(inputFile.delete()){
                            Log.d(TAG," deleted file after encryption");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        return "Error";
                    }

                }

            return "Successfully encrypted all files";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("test","Encrypting file");

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //value[0] is filename and value[1] is file size
            mTextView.setText("Encrypting file: "+values[0] +" ("+values[1]+" MBs)");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(IntermediateActivity.this, s, Toast.LENGTH_LONG).show();
            //start the file manager
            Intent intent=new Intent(IntermediateActivity.this,FileBrowserActivity.class);
            startActivity(intent);
        }
        /* Checks if external storage is available for read and write */
        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }
        /**
         * Round to certain number of decimals
         *
         * @param d
         * @param decimalPlace the numbers of decimals
         * @return
         */

        public float round(float d, int decimalPlace) {
            return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
        }

    }

    

}
