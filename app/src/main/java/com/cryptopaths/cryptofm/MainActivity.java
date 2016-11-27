package com.cryptopaths.cryptofm;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.KeyManagement;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Security;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private TextView        emailTextView;
    private TextView        passwordTextView;
    private DatabaseHandler mDatabaseHandler;

    private static final String TAG         ="MainActivity";
    private static final int RC_PERMISSION  =1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //map the references
        setVariablesReferences();
    }
    private void setVariablesReferences(){
        this.emailTextView      =(TextView)findViewById(R.id.input_email);
        this.passwordTextView   =(TextView)findViewById(R.id.input_password);
        mDatabaseHandler=new DatabaseHandler(this);
    }

    /**
     * when user clicks on generate keys button this method will be invoked. It is action method for
     * button. It start the process for generating keys.
     * @param v the view for which this function is handler.
     */
    @ActionHandler
    public void onGenerateKeysButtonClick(View v){
        //check if user enter required information correctly
       assert emailTextView!=null;
        assert passwordTextView!=null;
        if(!isValidEmail(emailTextView.getText())){
            emailTextView.setError("Please enter valid email");
            return;
        }
        if(!isValidPassword(passwordTextView.getText())){
            passwordTextView.setError("Password length should be greater than 4");
            return;
       }
        //gain storage permissions
       checkPermissionAndGenerate();


    }


    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private boolean isValidPassword(CharSequence password){
        if(password.length()>2){
            return true;
        }else{
            return false;
        }
    }
    @AfterPermissionGranted(RC_PERMISSION)
    private void checkPermissionAndGenerate(){
        String[] perms={Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this,perms)){
            startGeneratingKeys();
        }else{
            EasyPermissions.requestPermissions(this,getString(R.string.permission_string),
                    RC_PERMISSION,perms);
        }
    }

    private void startGeneratingKeys(){
        String email=emailTextView.getText().toString();
        String password=passwordTextView.getText().toString();
        new KeyGenerationTask().execute(email,password);
    }
    ProgressDialog mLoading;

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    //async task
    private class KeyGenerationTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String email=strings[0];
            char[] password=strings[1].toCharArray();
            KeyManagement keyManagement=new KeyManagement();
            try {
                Log.d(TAG,"start generating keys");
                PGPKeyRingGenerator keyRingGenerator=keyManagement.generateKey(email,password);
                PGPPublicKeyRing publicKeys=keyRingGenerator.generatePublicKeyRing();
                PGPSecretKeyRing secretKeys=keyRingGenerator.generateSecretKeyRing();
                //save keys in db
                //convert keys in String format
                String pubKeyString= Base64.encodeToString(publicKeys.getPublicKey().getEncoded(),Base64.DEFAULT);
                String secKeyString=Base64.encodeToString(secretKeys.getSecretKey().getEncoded(),Base64.DEFAULT);
                //call the db methods to store
                mDatabaseHandler.insertSecKey(email,secKeyString,pubKeyString);
                //create  a folder for keys
                File myDir=new File("/sdcard/PiercingArrow");
                if(!myDir.exists()){
                    Log.d(TAG,"Creating directory");
                    myDir.mkdir();
                }

                //output keys in ascii armored format
                ArmoredOutputStream pubOut=new ArmoredOutputStream(new FileOutputStream("/sdcard/PiercingArrow/pub.asc"));
                publicKeys.encode(pubOut);
                pubOut.close();
                ArmoredOutputStream secOut=new ArmoredOutputStream(new FileOutputStream("/sdcard/PiercingArrow/sec.asc"));
                secretKeys.encode(secOut);
                secOut.close();

                Log.d(TAG,"secret key written to file");
                //put key in database
                String secKeyTex=secretKeys.getSecretKey().getEncoded().toString();

                return "Key generation successful";



            } catch (Exception e) {
                Log.d(TAG,"Error generating keys");
                e.printStackTrace();
                return "Cannot generate keys";

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show a progress dialog
            mLoading=new ProgressDialog(MainActivity.this);
            mLoading.setMessage("wait while generating keys...");
            mLoading.setTitle("generating keys");
            mLoading.setCancelable(false);
            mLoading.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mLoading.dismiss();
            Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
        }
    }
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);

    }
}
