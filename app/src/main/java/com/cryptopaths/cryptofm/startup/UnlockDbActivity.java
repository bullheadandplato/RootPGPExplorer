package com.cryptopaths.cryptofm.startup;

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

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.filemanager.ui.FileBrowserActivity;
import com.cryptopaths.cryptofm.utils.ActionHandler;

import net.sqlcipher.database.SQLiteDatabase;

public class UnlockDbActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_db);
        setResult(RESULT_OK);
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
            String path = Environment.getExternalStorageDirectory().getPath();
            DatabaseHandler handler=new DatabaseHandler(UnlockDbActivity.this);
            if(handler.checkPass(pass)){
                //load data to fill file listview
                //FileBrowserActivity.mFilesData.put(path+"/",new FileFillerWrapper(path+"/",UnlockDbActivity.this));
                return true;
            }else{
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            if(result){
                Intent intent=new Intent(UnlockDbActivity.this,FileBrowserActivity.class);
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
