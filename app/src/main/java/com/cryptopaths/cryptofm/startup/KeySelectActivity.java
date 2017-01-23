package com.cryptopaths.cryptofm.startup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.KeyManagement;
import com.cryptopaths.cryptofm.encryption.MyPGPUtil;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.ui.FileBrowserActivity;
import com.cryptopaths.cryptofm.startup.fragments.PasswordsFragment;
import com.cryptopaths.cryptofm.utils.ActionHandler;
import com.cryptopaths.cryptolib.org.spongycastle.bcpg.ArmoredOutputStream;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPException;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPublicKey;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSecretKey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by tripleheader on 1/16/17.
 * activity
 */

public class KeySelectActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private static final int GET_PUBLIC_KEY_CODE=10;
    private static final int GET_SECRET_KEY_CODE=20;
    private static final String TAG             ="keySelectActivity";
    private static final int RC_PERMISSION          = 101;
    private String mSecretKeyFilename;
    private TextView mPubKeyEditText;
    private TextView mSecKeyEditText;
    private boolean IS_DIFFERENT_PASSWORD=false;
    private String mDbPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_select);

        mSecKeyEditText=(TextView) findViewById(R.id.sec_key_edit_text);
        checkPermissions();
    }
    @ActionHandler(layoutResource = R.id.button_letsgo_keys_select)
    public void onImportKeys(View view) {
        String errorMessageLength       = "password length should be greater than 3";
        String errorMessageMatch        = "password does not match";
        EditText keyPasswordEditText    = (EditText)findViewById(R.id.user_secret_key_pass_edit);
        if(!isValidPassword(keyPasswordEditText.getText())){
            keyPasswordEditText.setError(errorMessageLength);
            return;
        }
            if(IS_DIFFERENT_PASSWORD){
                EditText passwordEdit2          = (EditText)findViewById(R.id.password_databse);
                EditText confirmPasswordEdit2   = (EditText)findViewById(R.id.password_confirm_database);


                CharSequence password2 = passwordEdit2.getText();
                //check if password is valid
                if(isValidPassword(password2)){
                    if(password2.toString().equals(confirmPasswordEdit2.getText().toString())){
                        mDbPass = password2.toString();
                    }else{
                        confirmPasswordEdit2.setError(errorMessageMatch);
                        // password do not match get back
                        return;
                    }
                }else{
                    ((EditText)( findViewById(R.id.password_databse))).setError(errorMessageLength);
                    return;
                }
            }else{
                mDbPass=keyPasswordEditText.getText().toString();
            }
        new KeysSetupTask().execute();

    }
    public void onBrowseButtonClick(View view){
        if(checkPermissions()){
           if(view.getId()==R.id.button_select_secret_key){
                startFileBrowsing(GET_SECRET_KEY_CODE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedData.STARTED_IN_SELECTION_MODE=false;
        Log.d(TAG, "onActivityResult: Got the result");
        if(resultCode!=RESULT_OK){
            return;
        }
        String filename=data.getExtras().getString("filename");
        switch (requestCode){

            case GET_SECRET_KEY_CODE:{
                Log.d(TAG, "onActivityResult: Got the result back i.e secret key");
                this.mSecKeyEditText.setText("filename: "+filename);
                this.mSecretKeyFilename=filename;
                break;
            }
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
            Toast.makeText(
                    this,
                    "Need storage permissions to continue",
                    Toast.LENGTH_LONG
            ).show();
    }
    private boolean checkPermissions() {
        Log.d("man", "checkPermissions: im called dsds");
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_string),
                    RC_PERMISSION, perms);
        }
        return false;
    }
    private void startFileBrowsing(int requestCode){
        Intent intent=new Intent(this, FileBrowserActivity.class);
        intent.putExtra("select",true);
        startActivityForResult(intent,requestCode);
    }

    @ActionHandler(layoutResource = R.id.checkBox)
    public void showDatabasePasswordFragment(View view) {
        CheckBox b=(CheckBox)view;
        if(b.isChecked()){
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.password2_layout_select_activity,new PasswordsFragment())
                    .commit();
            IS_DIFFERENT_PASSWORD=true;
        }else{
            getSupportFragmentManager().
                    beginTransaction().
                    remove(
                            getSupportFragmentManager().
                                    findFragmentById(R.id.password2_layout_select_activity)
                    ).commit();
            IS_DIFFERENT_PASSWORD=false;
        }
    }
    class KeysSetupTask extends AsyncTask<Void,String,Boolean>{
        String uid="test";

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                PGPSecretKey key=MyPGPUtil.readSecretKey(mSecretKeyFilename);
                // if secret key is correct get the public key from it and save it
                publishProgress("Getting public key");
                PGPPublicKey pub=key.getPublicKey();
                //output keys in ascii armored format
                File file                   = new File(getFilesDir(),"pub.asc");
                ArmoredOutputStream pubOut  = new ArmoredOutputStream(new FileOutputStream(file));
                pub.encode(pubOut);
                pubOut.close();

                publishProgress("Setting up database");
                DatabaseHandler db=new DatabaseHandler(KeySelectActivity.this,mDbPass,false);
                ByteArrayOutputStream outputStream  = new ByteArrayOutputStream();
                ArmoredOutputStream secOut          = new ArmoredOutputStream(outputStream);
                key.encode(secOut);
                secOut.close();
                if(key.getUserIDs().hasNext()){
                    uid=(String) key.getUserIDs().next();
                }
                Log.d(TAG, "doInBackground: key successfully imported used id isL "+uid);
                byte[] test=outputStream.toByteArray();
                //call the db methods to store
                db.insertSecKey(uid,test);
                //save shared preferences
                SharedPreferences prefs=getSharedPreferences("done", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putString("username",uid);
                editor.putBoolean("done",true);
                editor.apply();
                editor.commit();
                return true;

            } catch (PGPException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){

                //start the unlock db activity
                Intent intent=new Intent(KeySelectActivity.this,UnlockDbActivity.class);
                intent.putExtra("username",uid);
                startActivityForResult(intent,0);
                finish();
            }
        }
    }
    private boolean isValidPassword(CharSequence password){
        return password.length() > 2;
    }

}
