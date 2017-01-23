package com.cryptopaths.cryptofm.startup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.KeyManagement;
import com.cryptopaths.cryptofm.startup.fragments.InitActivityFirstFragment;
import com.cryptopaths.cryptofm.startup.fragments.InitActivityThirdFragment;
import com.cryptopaths.cryptofm.startup.fragments.PasswordsFragment;
import com.cryptopaths.cryptofm.utils.ActionHandler;
import com.cryptopaths.cryptolib.org.spongycastle.bcpg.ArmoredOutputStream;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPKeyRingGenerator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPublicKeyRing;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSecretKeyRing;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Security;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class InitActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks , InitActivityThirdFragment.FragmentCreated{
    private static final int RC_PERMISSION          = 101;
    private static final String TAG                 = "InitActivity";
    private static int FRAGMENT_ONE_NUMBER          = 0;
    private static int FRAGMENT_TWO_NUMBER          = 1;
    private static Boolean IS_DIFFERENT_PASSWORD    = false;

    static {
        Security.insertProviderAt(new com.cryptopaths.cryptolib.org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private DatabaseHandler mDatabaseHandler;
    private InitActivityFirstFragment mFirstFragment;
    private String          mUserSecretDatabase;
    private String          mUserSecretKeyPassword;
    private String          mUserName;
    private ProgressBar     mDatabaseProgressBar;
    private ProgressBar     mKeygenProgressBar;
    private Drawable        mProgressBarDefaultDrawable;
    private Drawable        mProgressBarAfterDrawable;
    private static Boolean        mGettingPermission=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Creating activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        setResult(RESULT_OK);

        //first of all check shared preferences
        SharedPreferences preferences   = getPreferences(Context.MODE_PRIVATE);
        boolean isNotFirstRun           = preferences.getBoolean("key",false);
        String username                 = preferences.getString("username","default");
        if(isNotFirstRun){
            //change activity to unlock db activity
            Intent intent = new Intent(this,UnlockDbActivity.class);
            intent.putExtra("username",username);
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
                    replace(R.id.password2_layout,new PasswordsFragment())
                    .commit();
            IS_DIFFERENT_PASSWORD=true;
        }else{
            getSupportFragmentManager().
                    beginTransaction().
                    remove(
                            getSupportFragmentManager().
                                    findFragmentById(R.id.password2_layout)
                    ).commit();
            IS_DIFFERENT_PASSWORD=false;
        }

    }

    @ActionHandler(layoutResource = R.id.next_button)
    public void onNextButtonClick(View v){
        EditText passwordEditText       = (EditText)findViewById(R.id.password);
        EditText passwordConfirm1       = (EditText)findViewById(R.id.password_confirm);
        EditText usernameEdit           = (EditText)findViewById(R.id.username_edittext);

        String errorMessageLength       = "password length should be greater than 3";
        String errorMessageMatch        = "password does not match";
        CharSequence sequence           = passwordEditText.getText();
        CharSequence sequenceConfirm    = passwordConfirm1.getText();

        if(usernameEdit.getText().toString().length()<1){
            usernameEdit.setError("Please give me your name");
            return;
        }
        mUserName=usernameEdit.getText().toString();

        //check if user wants different passwords
        if(IS_DIFFERENT_PASSWORD){
            EditText passwordEdit2          = (EditText)findViewById(R.id.password_databse);
            EditText confirmPasswordEdit2   = (EditText)findViewById(R.id.password_confirm_database);

            CharSequence password2 = passwordEdit2.getText();
            //check if password is valid
            if(isValidPassword(password2)){
                if(password2.toString().equals(confirmPasswordEdit2.getText().toString())){
                    mUserSecretDatabase = password2.toString();
                }else{
                    confirmPasswordEdit2.setError(errorMessageMatch);
                    // password do not match get back
                    return;
                }
            }else{
                ((EditText)( findViewById(R.id.password_databse))).setError(errorMessageLength);
                return;
            }

        }
            if(isValidPassword(sequence)){
                if(sequence.toString().equals(sequenceConfirm.toString())) {
                    Log.d("password","one password and two: "+sequence +" : "+sequenceConfirm);
                    mUserSecretKeyPassword = sequence.toString();
                    if(!IS_DIFFERENT_PASSWORD){
                        mUserSecretDatabase=mUserSecretKeyPassword;
                    }
                    Boolean permission=checkPermissions();
                    if(permission){
                        //replace fragment to second fragment
                        Log.d(TAG, "onNextButtonClick: aor mera b bnta hai");
                        replaceFragment(FRAGMENT_TWO_NUMBER);
                    } else{
                        // get read and write storage permission
                        Log.d(TAG, "onNextButtonClick: mera execute hona bnta hai");
                        //do nothing
                    }

                }else{
                    passwordConfirm1.setError(errorMessageMatch);
                }
            }else{
                passwordEditText.setError(errorMessageLength);
            }

        /*
        set if different password to false . to avoid exception
        if user switch fragments
         */
        ((CheckBox)findViewById(R.id.checkBox)).setChecked(false);
            IS_DIFFERENT_PASSWORD=false;


    }

    Boolean isInThirdFragment = false;
    Boolean isInFirstFragment = false;
    private void replaceFragment(int fragmentNumber){
        Fragment fragment;
        switch (fragmentNumber){
            case 0:
                mFirstFragment=new InitActivityFirstFragment();
                fragment=mFirstFragment;
                isInFirstFragment=true;
                break;
            case 1:
                fragment=new InitActivityThirdFragment();
                // in third fragment user cannot go back so empty the backstack.
                //twice because only two fragments are there first, we are sure about this
                isInThirdFragment=true;
                break;
            default:
                return;
        }
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right).
                    replace(R.id.fragment_frame_layout, fragment).
                    commit();
            mGettingPermission=false;

    }

    private void setupProgressBarsAndExecute(){
        Log.d("fragment","fragment three should be created");
        mKeygenProgressBar          =(ProgressBar)findViewById(R.id.key_progressbar);
        mDatabaseProgressBar        =(ProgressBar)findViewById(R.id.db_progressbar);

        mProgressBarDefaultDrawable = mDatabaseProgressBar.getIndeterminateDrawable();
        mProgressBarAfterDrawable   = getDrawable(R.drawable.ic_check_circle_white_48dp);
        //change intermediate drawables
        mKeygenProgressBar.setIndeterminateDrawable(mProgressBarAfterDrawable);


        //execute
        new DatabaseSetupTask().execute();


    }
    private void commitInitActivity() {
        //put in shared preferences
        SharedPreferences preferences=getSharedPreferences("done",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("done",true);
        editor.putString("username",mUserName);
        editor.apply();
        editor.commit();
    }

    private boolean isValidPassword(CharSequence password){
        return password.length() > 2;
    }

    /*
    Permission area
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode==RC_PERMISSION){
            //change fragment to second fragment
         //  replaceFragment(FRAGMENT_TWO_NUMBER);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private boolean checkPermissions(){
        Log.d("man", "checkPermissions: im called dsds");
        String[] perms  = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
        if(EasyPermissions.hasPermissions(this,perms)){
               return true;
        }else{
            mGettingPermission=false;
            EasyPermissions.requestPermissions(this,getString(R.string.permission_string),
                    RC_PERMISSION,perms);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case RC_PERMISSION:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"permissions granted");
                    //change fragment to second fragment
                    mGettingPermission=true;
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Resuming activity");
        super.onResume();
        if(mGettingPermission){
            replaceFragment(FRAGMENT_TWO_NUMBER);

        }

    }

    @Override
    public void onBackPressed() {
        if(isInFirstFragment){
            super.onBackPressed();
        }
        if(!isInThirdFragment){
            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_left).
                    replace(R.id.fragment_frame_layout, mFirstFragment).
                    commit();
        }else{
            Toast.makeText(
                    this,
                    "Please wait while I finish setup",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onThirdFragmentCreated() {
        Log.d("fragment","yes the fragment created");
        setupProgressBarsAndExecute();
    }

    /*
    Generating keys area
     */
    private class KeyGenerationTask extends AsyncTask<Void,Void,byte[]> {

        @Override
        protected byte[] doInBackground(Void... strings) {
            String email                = mUserName;
            char[] password             = mUserSecretKeyPassword.toCharArray();
            KeyManagement keyManagement = new KeyManagement();
            try {
                Log.d(TAG,"start generating keys");
                PGPKeyRingGenerator keyRingGenerator    = keyManagement.generateKey(email,password);
                PGPPublicKeyRing publicKeys             = keyRingGenerator.generatePublicKeyRing();
                PGPSecretKeyRing secretKeys             = keyRingGenerator.generateSecretKeyRing();

                //output keys in ascii armored format
                File file                   = new File(getFilesDir(),"pub.asc");
                ArmoredOutputStream pubOut  = new ArmoredOutputStream(new FileOutputStream(file));
                publicKeys.encode(pubOut);
                pubOut.close();

                ByteArrayOutputStream outputStream  = new ByteArrayOutputStream();
                ArmoredOutputStream secOut          = new ArmoredOutputStream(outputStream);
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
            mKeygenProgressBar.setIndeterminateDrawable(mProgressBarDefaultDrawable);

        }

        @Override
        protected void onPostExecute(byte[] s) {
            super.onPostExecute(s);
            mKeygenProgressBar.setIndeterminateDrawableTiled(mProgressBarAfterDrawable);
            //commit changes
            commitInitActivity();
            //start intermediateActivity
            Intent intent = new Intent(InitActivity.this,UnlockDbActivity.class);
            intent.putExtra("username",mUserName);
            startActivityForResult(intent,1);
            finish();

        }
    }
    private class DatabaseSetupTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteDatabase.loadLibs(InitActivity.this);
            mDatabaseHandler = new DatabaseHandler(
                    InitActivity.this,
                    mUserSecretDatabase,
                    false
            );
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDatabaseProgressBar.setIndeterminateDrawableTiled(mProgressBarAfterDrawable);
            //start the key generation task
            new KeyGenerationTask().execute();
        }
    }



}
