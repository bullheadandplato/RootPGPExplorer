package com.cryptopaths.cryptofm.startup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.encryption.KeyManagement;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.utils.ActionHandler;
import com.cryptopaths.cryptolib.org.spongycastle.bcpg.ArmoredOutputStream;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPKeyRingGenerator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPublicKeyRing;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSecretKeyRing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class OptionActivity extends AppCompatActivity {
    private String mKeyPassword;
    private static final String TAG=OptionActivity.class.getName();

    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_options);
        mProgressDialog=new ProgressDialog(this);

    }
    @ActionHandler(layoutResource = R.id.button_generate_key)
    public void chooseNextActivity(View view){
        if(view.getId()==R.id.button_generate_key){
            // start generating keys
            generateKeys();
        }else{
            Intent intent=new Intent(this,KeySelectActivity.class);
            startActivity(intent);
        }
    }
    private void generateKeys(){
        //show password dialog
        final Dialog dialog=new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.passwords_fragment_layout);
        dialog.setTitle("keys password");
        final TextInputEditText editText=(TextInputEditText)dialog.findViewById(R.id.gen_keys_password_edit);
        final TextInputEditText confirmEditText=(TextInputEditText)dialog.findViewById(R.id.gen_keys_password_edit_confirm);


        dialog.findViewById(R.id.trigger_gen_key_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //test the passwords
                if (isValidPassword(editText.getText())) {
                    if (editText.getText().equals(confirmEditText.getText())) {
                        //be sure to dismiss the dialog
                        dialog.dismiss();
                        //start the async task
                        mKeyPassword = editText.getText().toString();
                        new KeyGenerationTask().execute();
                    } else {
                        confirmEditText.setError("Password does not match");
                    }
                } else {
                    editText.setError("Password should contain at least three characters");
                }


            }
        });
    }
    private boolean isValidPassword(CharSequence password){
        return password.length() > 2;
    }

    /*
    Generating keys area
    */
    private class KeyGenerationTask extends AsyncTask<Void,Void,byte[]> {

        @Override
        protected byte[] doInBackground(Void... strings) {
            String email                = SharedData.USERNAME;
            char[] password             = mKeyPassword.toCharArray();
            try {
                Log.d(TAG,"start generating keys");
                PGPKeyRingGenerator keyRingGenerator    = new KeyManagement().generateKey(email,password);
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

                DatabaseHandler db=new DatabaseHandler(OptionActivity.this,SharedData.DB_PASSWWORD,true);
                byte[] test=outputStream.toByteArray();
                //call the db methods to store
                db.insertSecKey(email,test);
                SharedPreferences prefs=getSharedPreferences("done", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("keys_gen",true);
                editor.apply();
                editor.commit();
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
            mProgressDialog.setTitle("Generating keys");
            mProgressDialog.setMessage("Pleas wait it can take a while");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected void onPostExecute(byte[] s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            SharedData.KEYS_GENERATED=true;
            finish();

        }
    }
}
