package com.cryptopaths.cryptofm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

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
        mPubKeyFile=new File("/sdcard/Piercingarrow/pub.asc");
        mEncryptionManagement=new EncryptionManagement();
        testIt();
    }
    private void testIt(){
        //first encrypt a file
        File test=new File("/sdcard/Download/test.jpg");
        File outputFile=new File("/sdcard/Download/test.gpg");
        try {
            Log.d("test","Encrypting file");
            outputFile.createNewFile();
            mEncryptionManagement.encryptFile(outputFile,test,mPubKeyFile);
            File outTest=new File("/sdcard/Piercingarrow/test.jpg");
            outTest.createNewFile();
                //now decrypt file
                Log.d("test","decrypting file");
                test.createNewFile();
                mEncryptionManagement.decryptFile(outputFile,outTest,mPubKeyFile,
                        mSecKeyFile,"google".toCharArray());
                Log.d("test","success file");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("test","Error file");

        }
    }
}
