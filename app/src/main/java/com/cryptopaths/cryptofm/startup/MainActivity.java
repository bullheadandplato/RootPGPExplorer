package com.cryptopaths.cryptofm.startup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.encryption.DatabaseHandler;
import com.cryptopaths.cryptofm.utils.ActionHandler;

import java.security.Security;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private TextView        emailTextView;
    private TextView        passwordTextView;
    private DatabaseHandler mDatabaseHandler;


    private static final String TAG         ="MainActivity";
    private static final int RC_PERMISSION  =1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //first of all check shared preferences
        SharedPreferences preferences   =getPreferences(Context.MODE_PRIVATE);
        boolean isNotFirstRun           =preferences.getBoolean("key",false);
        if(isNotFirstRun){

            //get the secret key from db
            String userEmail    =preferences.getString("mail",null);
            mDatabaseHandler    =new DatabaseHandler(this,"google",false);
            byte[] key          =mDatabaseHandler.getSecretKeyFromDb(userEmail);
            //start IntermediateActivity
            startIntermediateActivity(key);
        }else{
            //map the references
            setVariablesReferences();
        }


    }



    private void setVariablesReferences(){
        this.emailTextView      =(TextView)findViewById(R.id.input_email);
        this.passwordTextView   =(TextView)findViewById(R.id.input_password);
        this.mDatabaseHandler   =new DatabaseHandler(this,"google",true);

    }

    /**
     * when user clicks on generate keys button this method will be invoked. It is action method for
     * button. It start the process for generating keys.
     * @param v the view for which this function is handler.
     */
    @ActionHandler(layoutResource = R.id.btn_login)
    public void onGenerateKeysButtonClick(View v){
        //check if user enter required information correctly
        assert emailTextView    !=null;
        assert passwordTextView !=null;
        if(!isValidEmail(emailTextView.getText())){
            emailTextView.setError("Please enter valid email");
            return;
        }
        if(!isValidPassword(passwordTextView.getText())){
            passwordTextView.setError("Password length should be greater than 4");
            return;
       }
        //gain storage permissions
       checkPermissionAndGenerate();


    }


    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private boolean isValidPassword(CharSequence password){
        if(password.length()>2){
            return true;
        }else{
            return false;
        }
    }
    @AfterPermissionGranted(RC_PERMISSION)
    private void checkPermissionAndGenerate(){
        String[] perms  ={Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this,perms)){
           // startGeneratingKeys();
        }else{
            EasyPermissions.requestPermissions(this,getString(R.string.permission_string),
                    RC_PERMISSION,perms);
        }
    }

    private void startGeneratingKeys(){
        String email    =emailTextView.getText().toString();
        String password =passwordTextView.getText().toString();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);

    }
    private void startIntermediateActivity(byte[] key) {
        Intent intent=new Intent(MainActivity.this,IntermediateActivity.class);
        intent.putExtra("key",key);
        startActivity(intent);
    }
}
