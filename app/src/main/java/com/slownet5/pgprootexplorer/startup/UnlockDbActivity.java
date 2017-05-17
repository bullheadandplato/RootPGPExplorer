/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.startup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.encryption.DatabaseHandler;
import com.slownet5.pgprootexplorer.filemanager.ui.FileManagerActivity;
import com.slownet5.pgprootexplorer.utils.ActionHandler;

import net.sqlcipher.database.SQLiteDatabase;

public class UnlockDbActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_db);
        setResult(RESULT_OK);
        findViewById(R.id.button_unlock_db).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnlockButtonClick(v);
            }
        });
    }

    @ActionHandler(layoutResource = R.id.button_unlock_db)
    public void onUnlockButtonClick(View view){
        EditText passwordEditText=
                (EditText)findViewById(R.id.input_password__unlock);
        String pass=passwordEditText.getText().toString();
        SQLiteDatabase.loadLibs(this);
        new IntialTask().execute(
                pass,
                Environment.getExternalStorageDirectory().getPath()
        );
    }

    private void showErrorDialog(String s) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(s);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing.
            }
        });
        builder.show();
    }

    private ProgressDialog mProgressDialog;

    private class IntialTask extends AsyncTask<String,Void,Boolean>{
        String pass;
        @Override
        protected Boolean doInBackground(String... voids) {
            pass        = voids[0];
            DatabaseHandler handler=new DatabaseHandler(UnlockDbActivity.this);
            return handler.checkPass(pass);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            if(result){
                Intent intent=new Intent(UnlockDbActivity.this,FileManagerActivity.class);
                intent.putExtra("dbpass",pass);
                intent.putExtra("username",getIntent().getExtras().getString("username"));
                startActivityForResult(intent,1);
                finish();
            }else{
                showErrorDialog("Wrong password, please try again");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(UnlockDbActivity.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("please wait....");
            mProgressDialog.show();
        }
    }
}
