/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofmroot.startup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.encryption.DatabaseHandler;
import com.osama.cryptofmroot.encryption.KeyManagement;
import com.osama.cryptofmroot.filemanager.utils.SharedData;
import com.osama.cryptofmroot.utils.ActionHandler;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class OptionActivity extends AppCompatActivity {
    private String mKeyPassword;
    private static final String TAG=OptionActivity.class.getName();
    private static final int RC_RESULT=121;

    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_options);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));
        }
        mProgressDialog=new ProgressDialog(this);
        findViewById(R.id.button_select_key).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNextActivity(v);
            }
        });
        findViewById(R.id.button_generate_key).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNextActivity(v);
            }
        });

    }
    @ActionHandler(layoutResource = R.id.button_generate_key)
    public void chooseNextActivity(View view){
        if(view.getId()==R.id.button_generate_key){
            // start generating keys
            generateKeys();
        }else{
            Intent intent=new Intent(this,KeySelectActivity.class);
            startActivityForResult(intent,RC_RESULT);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode==RC_RESULT){
           if(resultCode==RESULT_OK){
               //means browse keys was successful
               finish();
           }
       }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generateKeys(){
        //show password dialog
        final Dialog dialog=new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.passwords_fragment_layout);
        dialog.setTitle("keys password");
        final TextInputEditText editText=(TextInputEditText)dialog.findViewById(R.id.gen_keys_password_edit);
        final TextInputEditText confirmEditText=(TextInputEditText)dialog.findViewById(R.id.gen_keys_password_edit_confirm);
        dialog.show();

        dialog.findViewById(R.id.trigger_gen_key_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //test the passwords
                if (isValidPassword(editText.getText())) {
                    if (editText.getText().toString().equals(confirmEditText.getText().toString())) {
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
        dialog.findViewById(R.id.cancel_dialog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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

                DatabaseHandler db=new DatabaseHandler(OptionActivity.this,SharedData.DB_PASSWORD,true);
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
            if(s!=null){
                Toast.makeText(
                        OptionActivity.this,
                        "Successfully generated keys. Now you can encrypt files",
                        Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                SharedData.KEYS_GENERATED=true;
                finish();
            }else{
                 Toast.makeText(
                        OptionActivity.this,
                        "There is an error in generating keys, please try again!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}
