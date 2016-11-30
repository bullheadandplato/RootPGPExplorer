package com.cryptopaths.cryptofm;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.cryptopaths.cryptofm.encryption.DatabaseHandler;

public class UnlockDbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_db);
    }
    @ActionHandler(layoutResource = R.id.input_password__unlock)
    public void onUnlockButtonClick(View view){
        EditText passwordEditText=
                (EditText)findViewById(R.id.input_password__unlock);
        String pass=passwordEditText.getText().toString();
        DatabaseHandler handler=new DatabaseHandler(this);
        if(handler.checkPass(pass)){
            //start the new activity as user is faithful
            Intent intent=new Intent(this,FileBrowserActivity.class);
            startActivity(intent);
        }else{
            showErrorDialog("Wrong password");
        }
    }
    private void showErrorDialog(String s) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(s);
        builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing.
            }
        });
        builder.show();
    }
}
