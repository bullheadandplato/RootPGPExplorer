package com.osama.cryptofmroot.extras;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.ui.FileManagerActivity;
import com.osama.cryptofmroot.filemanager.utils.UiUtils;
import com.osama.cryptofmroot.root.RootUtils;
import com.osama.cryptofmroot.utils.CommonConstants;
import com.osama.cryptofmroot.utils.FileUtils;

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

    private File mFile;
    private EditText mEditText;
    private ProgressDialog mProgressDialog;
    private boolean isNewFile=false;
    private String mPath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);
        mEditText=(EditText) findViewById(R.id.editor_area);
        mProgressDialog =new ProgressDialog(this);
        
        String path=getIntent().getExtras().getString(CommonConstants.TEXTEDITACT_PARAM_PATH);
        if(path!=null){
            mFile=new File(path);
            if(mFile.isDirectory()){
              isNewFile=true;
                mPath=mFile.getAbsolutePath();
            }else{
                mPath=path;
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
            case R.id.save_file_menuitem:
                saveFile();
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
                        new FileSaveTask().execute(folderName,mEditText.getText().toString());
                    }
                }
            });
        }else{
            new FileSaveTask().execute(mEditText.getText().toString());
        }
    }

    private class FileSaveTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String text = params[1];
                String filename=params[0];
                if(RootUtils.isRootPath(mPath)){
                    Log.d(TAG, "doInBackground: full path and filename is: "+mPath+filename);
                    RootUtils.mountRw();
                    Shell.SU.run("echo \'"+text+"\' >> "+mPath+"/"+filename);
                    return true;
                }
                mFile=new File(mPath+"/"+filename);
                if(!mFile.exists()){
                    mFile.createNewFile();
                }
                BufferedWriter writer=new BufferedWriter(new FileWriter(mFile));
                writer.write(text);
                writer.close();
                writer.flush();
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
                mEditText.setText(builder.toString());
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
