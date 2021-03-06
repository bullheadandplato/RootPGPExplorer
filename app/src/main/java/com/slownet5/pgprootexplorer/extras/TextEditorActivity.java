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

package com.slownet5.pgprootexplorer.extras;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.utils.UiUtils;
import com.slownet5.pgprootexplorer.root.RootUtils;
import com.slownet5.pgprootexplorer.utils.CommonConstants;
import com.slownet5.pgprootexplorer.utils.FileUtils;
import com.slownet5.pgprootexplorer.utils.MainUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by bullhead on 5/2/17.
 *
 */

public class TextEditorActivity extends AppCompatActivity{
    private static final String TAG=TextEditorActivity.class.getCanonicalName();
    int prevNumber=1;

    private File mFile;
    private EditText mEditText;
    private ProgressDialog mProgressDialog;
    private boolean isNewFile=false;
    private String mPath;
    private KeyListener mkeylistener;
    private int mlength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);
        MainUtils.closeActionBarButton(this);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Editor");
        }
        mEditText=(EditText) findViewById(R.id.editor_area);
        mProgressDialog =new ProgressDialog(this);
        mlength=mEditText.getText().length();
        mkeylistener=mEditText.getKeyListener();
        mEditText.setKeyListener(null);
        mEditText.setEnabled(false);
        String path=getIntent().getExtras().getString(CommonConstants.TEXTEDITACT_PARAM_PATH);
        if(path!=null){
            mFile=new File(path);
            if(mFile.isDirectory()){
              isNewFile=true;
                mPath=mFile.getAbsolutePath();
            }else{
                mPath=path;
                Log.d(TAG, "onCreate: file path is: "+path);
                new FileOpenTask().execute();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                finish();
                break;
            case R.id.save_file_menuitem:
                saveFile();
                break;
            case R.id.edit_file_menuitem:
                mEditText.setKeyListener(mkeylistener);
                mEditText.setEnabled(true);
                break;

        }
        return true;
    }

    private void saveFile() {
        if (isNewFile) {
            isNewFile = false;
            final Dialog dialog = UiUtils.createDialog(
                    this,
                    "Save File",
                    "Save"
            );

            final EditText folderEditText = (EditText) dialog.findViewById(R.id.foldername_edittext);
            Button okayButton = (Button) dialog.findViewById(R.id.create_file_button);

            okayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String folderName = folderEditText.getText().toString();
                    if (folderName.length() < 1) {
                        folderEditText.setError("Give me the file name");
                    } else {
                        dialog.dismiss();
                        new FileSaveTask().execute(folderName,mEditText.getText().toString());
                    }
                }
            });
        }else{
            new FileSaveTask().execute("",mEditText.getText().toString());
        }

    }
    private class FileSaveTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String text = params[1];
                String filename;
                if(new File(mPath).isDirectory()){
                    filename=mPath+"/"+params[0];
                }else{
                    filename=mPath;
                }
                Log.d(TAG, "doInBackground: Path is: "+mPath);
                if(RootUtils.isRootPath(mPath+"/")){
                    Log.d(TAG, "doInBackground: full path and filename is: "+filename);
                    RootUtils.mountRw();
                    Shell.SU.run("echo \'"+text+"\' >> "+filename);
                    return true;
                }
                mFile=new File(filename);
                if(!mFile.exists()){
                    mFile.createNewFile();
                    //FileUtils.notifyChange(TextEditorActivity.this,mFile.getAbsolutePath());
                    Log.d(TAG, "doInBackground: inserting new file");
                    FileUtils.notifyChange(TextEditorActivity.this,mFile.getAbsolutePath());
                }
                BufferedWriter writer=new BufferedWriter(new FileWriter(mFile));
                writer.write(text);
                writer.flush();
                writer.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            if(aBoolean){
                Toast.makeText(TextEditorActivity.this,"File saved.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(TextEditorActivity.this,"Cannot save file.",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setTitle("Saving file");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();
        }
    }
    private class FileOpenTask extends AsyncTask<Void,Void,Boolean>{
        private StringBuilder builder;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(RootUtils.isRootPath(mPath)){
                    String filename=mFile.getName();
                    mFile=new File(getFilesDir()+"/"+filename);
                    mFile.createNewFile();
                    Shell.SU.run("cat "+mPath+" > "+getFilesDir()+"/"+filename);
                }
                Log.d(TAG, "doInBackground: mFile name is: "+mFile.getName());
                BufferedReader reader=new BufferedReader(new FileReader(mFile));
                builder=new StringBuilder();
                String line;
                while ((line=reader.readLine())!=null){
                    builder.append(line);
                }
                reader.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            if(aBoolean){
                try {
                    mEditText.setText(builder.toString());
                }catch (OutOfMemoryError error){
                    Toast.makeText(TextEditorActivity.this,"Unable to open file.",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(TextEditorActivity.this,"Cannot open file.",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setTitle("Opening file");
            mProgressDialog.setMessage("Please wait....");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }
}
