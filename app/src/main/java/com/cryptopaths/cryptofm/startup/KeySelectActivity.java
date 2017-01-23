package com.cryptopaths.cryptofm.startup;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.ui.FileBrowserActivity;
import com.cryptopaths.cryptofm.startup.fragments.PasswordsFragment;
import com.cryptopaths.cryptofm.utils.ActionHandler;

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

    private TextView mPubKeyEditText;
    private TextView mSecKeyEditText;
    private boolean IS_DIFFERENT_PASSWORD=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_select);

        mSecKeyEditText=(TextView) findViewById(R.id.sec_key_edit_text);
        mPubKeyEditText=(TextView) findViewById(R.id.pub_key_edit_text);
        checkPermissions();
    }
    @ActionHandler(layoutResource = R.id.button_letsgo_keys_select)
    public void onUnlockButtonClick(View view) {

    }
    public void onBrowseButtonClick(View view){
        if(checkPermissions()){
            if(view.getId()==R.id.button_select_public_key){
                startFileBrowsing(GET_PUBLIC_KEY_CODE);
            }else if(view.getId()==R.id.button_select_secret_key){
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

            case GET_PUBLIC_KEY_CODE:{
                this.mPubKeyEditText.setText("filename: "+filename);
                break;
            }
            case GET_SECRET_KEY_CODE:{
                Log.d(TAG, "onActivityResult: Got the result back i.e secret key");
                this.mSecKeyEditText.setText("filename: "+filename);
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
}
