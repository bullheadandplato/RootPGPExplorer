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
import com.cryptopaths.cryptofm.encryption.MyPGPUtil;
import com.cryptopaths.cryptofm.filemanager.ui.FileBrowserActivity;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
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

public class KeySelectActivity extends AppCompatActivity {

    private static final int        GET_SECRET_KEY_CODE     =20;
    private static final String     TAG                     ="keySelectActivity";
    private String                  mSecretKeyFilename;
    private TextView                mSecKeyEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_select);

        mSecKeyEditText=(TextView) findViewById(R.id.sec_key_edit_text);

    }
    @ActionHandler(layoutResource = R.id.button_letsgo_keys_select)
    public void onImportKeys(View view) {
        new KeysSetupTask().execute();

    }
    public void onBrowseButtonClick(View view){

           if(view.getId()==R.id.button_select_secret_key){
                startFileBrowsing(GET_SECRET_KEY_CODE);
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

    private void startFileBrowsing(int requestCode){
        Intent intent=new Intent(this, FileBrowserActivity.class);
        intent.putExtra("select",true);
        startActivityForResult(intent,requestCode);
    }

    class KeysSetupTask extends AsyncTask<Void,String,Boolean>{

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
                DatabaseHandler db=new DatabaseHandler(KeySelectActivity.this,SharedData.DB_PASSWWORD,true);
                ByteArrayOutputStream outputStream  = new ByteArrayOutputStream();
                ArmoredOutputStream secOut          = new ArmoredOutputStream(outputStream);
                key.encode(secOut);
                secOut.close();

                byte[] test=outputStream.toByteArray();
                //call the db methods to store
                db.insertSecKey(SharedData.USERNAME,test);
                //save shared preferences
                SharedPreferences prefs=getSharedPreferences("done", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("keys_gen",true);
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
                SharedData.KEYS_GENERATED=true;
                finish();
            }else{
                Toast.makeText(
                        KeySelectActivity.this,
                        "Cannot read secret key, make sure you have choose the right file",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
    private boolean isValidPassword(CharSequence password){
        return password.length() > 2;
    }

}
