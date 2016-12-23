package com.cryptopaths.cryptofm.startup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.KeyManagement;
import com.cryptopaths.cryptofm.utils.ActionHandler;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class InitActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private static final int RC_PICK_FILE_TO_SAVE_INTERNAL  =100;
    private static final int RC_PERMISSION                  =101;
    private int mFragmentNumber                             =0;
    private static final String TAG                         ="InitActivity";
    private static final int CONTENT_FRAGMENT_LAYOUT        =100;

    private DatabaseHandler     mDatabaseHandler;
    private ProgressDialog      mLoading;
    private ThirdFragment       mThirdFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        //first of all check shared preferences
        SharedPreferences preferences   =getPreferences(Context.MODE_PRIVATE);
        boolean isNotFirstRun           =preferences.getBoolean("key",false);
        if(isNotFirstRun){
            //change activity to unlock db activity
            Intent intent=new Intent(this,UnlockDbActivity.class);
            //clear the stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent,1);
            finish();

        }
        //add first fragment
        FrameLayout frameLayout=(FrameLayout) findViewById(R.id.fragment_frame_layout);
        frameLayout.setId(CONTENT_FRAGMENT_LAYOUT);
        FirstFragment fragment=new FirstFragment();
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right).
                replace(CONTENT_FRAGMENT_LAYOUT,fragment).
                commit();


    }
    @ActionHandler(layoutResource = R.id.next_button)
    public void onNextButtonClick(View v){
        switch (mFragmentNumber){
            case 0:
                EditText passwordEditText=
                        (EditText)findViewById(R.id.password);
                CharSequence sequence=passwordEditText.getText();
                if(isValidPassword(sequence)){
                    Log.d("fragment","replacing fragmnet "+mFragmentNumber);
                    //create encrypted database and set password of user choice
                    //mDatabaseHandler=new DatabaseHandler(this,sequence.toString(),false);
                    //change fragment to next fragment

                    if(checkPermissions()){
                        //first remove the logo image from activity
                        View logoImage=findViewById(R.id.logo_image);
                        ((ViewGroup)logoImage.getParent()).removeView(logoImage);
                        ThirdFragment secondFragment=new ThirdFragment();
                        getSupportFragmentManager().beginTransaction().
                                setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
                                        R.anim.enter_from_left, R.anim.exit_to_right).
                                replace(CONTENT_FRAGMENT_LAYOUT,secondFragment).
                                commit();
                        //set fragment number to 1
                        mFragmentNumber=1;
                    }else{
                        getPermissions();

                    }

                }else{
                    passwordEditText.setError("password length should be greater than 3");
                }
                Log.d("fragment","replacing fragmnet "+mFragmentNumber);
                break;
            case 1:


                break;
            case 2:
                //get selected directories
                assert mThirdFragment!=null;
                ArrayList<String> tmp=mThirdFragment.getAllSelectedPositions();
                if(tmp.size()>0){
                    //start the encryption activity as user selected directories
                    Intent intent=new Intent(this,IntermediateActivity.class);
                    intent.putExtra("dirs",tmp);
                    //commit that all in set up activity is done well.
                    commitInitActivity();
                    //clear stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent,0);
                    finish();
                }else {
                    Toast.makeText(this,
                            "Please choose one or more directories",
                            Toast.LENGTH_LONG
                    ).show();
                }

        }
    }



    private void commitInitActivity() {
        //put in shared preferences
        SharedPreferences preferences=getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("key",true);
        editor.apply();
        editor.commit();
    }

    private void getPermissions() {
        checkPermissions();
    }




    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private boolean isValidPassword(CharSequence password){
        return password.length() > 2;
    }

    /*
    Permission area
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG,"permissions granted");
        if (requestCode==RC_PERMISSION){
            //first remove the logo image from activity
            View logoImage=findViewById(R.id.logo_image);
            ((ViewGroup)logoImage.getParent()).removeView(logoImage);
            //change fragment to third fragment
            mThirdFragment=new ThirdFragment();
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right).
                    replace(CONTENT_FRAGMENT_LAYOUT,mThirdFragment).
                    commit();
            mFragmentNumber=2;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
    @AfterPermissionGranted(RC_PERMISSION)
    private boolean checkPermissions(){
        String[] perms  ={Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this,perms)){
               return true;
        }else{
            EasyPermissions.requestPermissions(this,getString(R.string.permission_string),
                    RC_PERMISSION,perms);
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);

    }

    /*
    Generating keys area
     */
    private class KeyGenerationTask extends AsyncTask<String,Void,byte[]> {

        @Override
        protected byte[] doInBackground(String... strings) {
            String email                =strings[0];
            char[] password             =strings[1].toCharArray();
            KeyManagement keyManagement =new KeyManagement();
            try {
                Log.d(TAG,"start generating keys");
                PGPKeyRingGenerator keyRingGenerator    =keyManagement.generateKey(email,password);
                PGPPublicKeyRing publicKeys             =keyRingGenerator.generatePublicKeyRing();
                PGPSecretKeyRing secretKeys             =keyRingGenerator.generateSecretKeyRing();

                //output keys in ascii armored format
                File file=new File(getFilesDir(),"pub.asc");
                ArmoredOutputStream pubOut=new ArmoredOutputStream(new FileOutputStream(file));
                publicKeys.encode(pubOut);
                pubOut.close();
                ByteArrayOutputStream outputStream  =new ByteArrayOutputStream();
                ArmoredOutputStream secOut          =new ArmoredOutputStream(outputStream);
                secretKeys.encode(secOut);
                secOut.close();
                byte[] test=outputStream.toByteArray();
                //call the db methods to store
                mDatabaseHandler.insertSecKey(email,test);

                Log.d(TAG,"secret key written to file");
                return  test;

            } catch (Exception e) {
                Log.d(TAG,"Error generating keys");
                e.printStackTrace();
                return null;

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show a progress dialog
            mLoading=new ProgressDialog(InitActivity.this);
            mLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mLoading.setIndeterminate(true);
            mLoading.setTitle("Generating keys");
            mLoading.setMessage("Please wait while generating keys");
            mLoading.setCancelable(false);
            mLoading.show();

        }

        @Override
        protected void onPostExecute(byte[] s) {
            super.onPostExecute(s);
            mLoading.dismiss();
            mFragmentNumber = 2;
            //make a toast the keys successfully generated
            Toast.makeText(InitActivity.this,
                    "Successfully generated keys",
                    Toast.LENGTH_LONG
            ).show();
            //get permission to read storage
            getPermissions();


        }
    }
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
