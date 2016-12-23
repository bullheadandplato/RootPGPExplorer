package com.cryptopaths.cryptofm.startup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private static final int RC_PERMISSION   = 101;
    private static final String TAG          = "InitActivity";
    private static int FRAGMENT_ONE_NUMBER   = 0;
    private static int FRAGMENT_TWO_NUMBER   = 1;
    private static int FRAGMENT_THREE_NUMBER = 2;
    private static Boolean IS_DIFFERENT_PASSWORD    = false;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private DatabaseHandler mDatabaseHandler;
    private ProgressDialog  mLoading;
    private SecondFragment  mSecondFragment;
    private String          mUserSecretDatabase;
    private String          mUserSecretKeyPassword;
    private ProgressBar     mDatabaseProgressBar;
    private ProgressBar     mKeygenProgressBar;
    private ProgressBar     mEncryptionProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        //first of all check shared preferences
        SharedPreferences preferences   = getPreferences(Context.MODE_PRIVATE);
        boolean isNotFirstRun           = preferences.getBoolean("key",false);
        if(isNotFirstRun){
            //change activity to unlock db activity
            Intent intent = new Intent(this,UnlockDbActivity.class);
            //clear the stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent,1);
            finish();

        }
        //add first fragment
        replaceFragment(FRAGMENT_ONE_NUMBER);


    }
    @ActionHandler(layoutResource = R.id.checkBox)
    public void showSecondPasswordCheckBox(View v){
        CheckBox b=(CheckBox)v;
        if(b.isChecked()){
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.frame_layout_password_two,new PasswordsFragment())
                    .commit();
            IS_DIFFERENT_PASSWORD=true;
        }else{
            getSupportFragmentManager().
                    beginTransaction().
                    remove(
                            getSupportFragmentManager().
                                    findFragmentById(R.id.frame_layout_password_two)
                    ).commit();
            IS_DIFFERENT_PASSWORD=false;
        }

    }

    @ActionHandler(layoutResource = R.id.next_button)
    public void onNextButtonClick(View v){
        EditText passwordEditText       =
                (EditText)findViewById(R.id.password);
        EditText passwordConfirm1       =
                (EditText)findViewById(R.id.password_confirm);

        String errorMessageLength       = "password length should be greater than 3";
        String errorMessageMatch        = "password does not match";
        CharSequence sequence           = passwordEditText.getText();
        CharSequence sequenceConfirm    = passwordConfirm1.getText();


        //check if user wants different passwords
        if(IS_DIFFERENT_PASSWORD){
            EditText passwordEdit2          =
                    (EditText)findViewById(R.id.password_databse);
            EditText confirmPasswordEdit2   =
                    (EditText)findViewById(R.id.password_confirm_database);

            CharSequence password2 = passwordEdit2.getText();
            //check if password is valid
            if(isValidPassword(password2)){
                if(password2.toString().equals(confirmPasswordEdit2.getText())){
                    mUserSecretDatabase = password2.toString();
                }else{
                    confirmPasswordEdit2.setError(errorMessageMatch);
                }
            }else{
                ((EditText)( findViewById(R.id.password_databse))).setError(errorMessageLength);
            }
        }
        if(isValidPassword(sequence)){
            if(sequence.toString().equals(sequenceConfirm.toString())) {
                Log.d("password","one password and two: "+sequence +" : "+sequenceConfirm);
                mUserSecretKeyPassword = sequence.toString();
                if(!IS_DIFFERENT_PASSWORD){
                    mUserSecretDatabase=mUserSecretKeyPassword;
                }
                if(checkPermissions()){
                    //first remove the logo image from activity
                    View logoImage=findViewById(R.id.logo_image);
                    ((ViewGroup)logoImage.getParent()).removeView(logoImage);
                    //replace fragment to second fragment
                    replaceFragment(FRAGMENT_TWO_NUMBER);
            } else{
                // get read and write storage permission
                getPermissions();
            }

        }else{
                passwordConfirm1.setError(errorMessageMatch);
            }
        }else{
            passwordEditText.setError(errorMessageLength);
        }



    }
    @ActionHandler(layoutResource = R.id.lets_go_button)
    public void onLetsGoButtonClick(View v){
        //get selected directories
        assert mSecondFragment !=null;
        ArrayList<String> tmp= mSecondFragment.getAllSelectedPositions();
        if(tmp.size()>0){
            //change fragment to third fragment
            replaceFragment(FRAGMENT_THREE_NUMBER);
        }else {
            Toast.makeText(
                    this,
                    "Please choose one or more directories",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    private void replaceFragment(int fragmentNumber){
        String fragmentBackName="";
        Fragment fragment;
        switch (fragmentNumber){
            case 0:
                fragmentBackName="first";
                fragment=new FirstFragment();

                break;
            case 1:
                fragmentBackName="second";
                fragment=new SecondFragment();
                // as I need it later
                mSecondFragment= (SecondFragment) fragment;
                break;
            case 2:
                fragmentBackName="third";
                fragment=new ThirdFragment();
                // in third fragment user cannot go back so empty the backstack.
                //twice because only two fragments are there first, we are sure about this
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().popBackStack();
                break;
            default:
                return;
        }
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right).
                replace(R.id.fragment_frame_layout, fragment).
                addToBackStack(fragmentBackName).
                commit();

    }


    private void commitInitActivity() {
        //put in shared preferences
        SharedPreferences preferences=getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("key",true);
        editor.apply();
        editor.commit();
    }

    private boolean isValidPassword(CharSequence password){
        return password.length() > 2;
    }

    /*
    Permission area
     */
    private void getPermissions() {
        checkPermissions();
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG,"permissions granted");
        if (requestCode==RC_PERMISSION){
            View logoImage=findViewById(R.id.logo_image);
            ((ViewGroup)logoImage.getParent()).removeView(logoImage);
            //change fragment to second fragment
            replaceFragment(FRAGMENT_TWO_NUMBER);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            //make a toast the keys successfully generated
            Toast.makeText(InitActivity.this,
                    "Successfully generated keys",
                    Toast.LENGTH_LONG
            ).show();
            //get permission to read storage
            getPermissions();


        }
    }
}
