package com.cryptopaths.cryptofm.startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.FileBrowserActivity;
import com.cryptopaths.cryptofm.utils.ActionHandler;

/**
 * Created by tripleheader on 1/16/17.
 * activity
 */

public class KeySelectActivity extends AppCompatActivity{
    private static final int GET_PUBLIC_KEY_CODE=10;
    private static final int GET_SECRET_KEY_CODE=20;
    private TextView mPubKeyEditText;
    private TextView mSecKeyEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_select);

        mSecKeyEditText=(TextView) findViewById(R.id.sec_key_edit_text);
        mPubKeyEditText=(TextView) findViewById(R.id.pub_key_edit_text);
    }
    @ActionHandler(layoutResource = R.id.button_letsgo_keys_select)
    public void onUnlockButtonClick(View view) {

    }
    public void onBrowseButtonClick(View view){
        if(view.getId()==R.id.button_select_public_key){

            Intent intent=new Intent(this, FileBrowserActivity.class);
            intent.putExtra("select",true);
            startActivityForResult(intent,GET_PUBLIC_KEY_CODE);
        }else if(view.getId()==R.id.button_select_secret_key){

            Intent intent=new Intent(this, FileBrowserActivity.class);
            intent.putExtra("select",true);
            startActivityForResult(intent,GET_SECRET_KEY_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filename=data.getExtras().getString("filename");
        switch (requestCode){

            case GET_PUBLIC_KEY_CODE:{
                this.mPubKeyEditText.setText(filename);
                break;
            }
            case GET_SECRET_KEY_CODE:{
                this.mSecKeyEditText.setText(filename);
                break;
            }
        }
        //check if user choose the same file twice
        if(mSecKeyEditText.getText().equals(mPubKeyEditText.getText())){
            Toast.makeText(
                    this,
                    "Secret key and the public key file cannot be the same",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
