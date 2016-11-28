package com.cryptopaths.cryptofm;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class InitActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private int mFragmentNumber=0;
    private FragmentTransaction mFragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        //set initial fragment
        mFragmentManager=getFragmentManager();
        mFragmentTransaction=mFragmentManager.beginTransaction();
        FirstFragment firstFragment=new FirstFragment();
        mFragmentTransaction.add(R.id.first_fragment,firstFragment);
        mFragmentTransaction.commit();
    }
    public void onNextButtonClick(View v){
        switch (mFragmentNumber){
            case 0:
                EditText passwordEditText=
                        (EditText)findViewById(R.id.input_password_first_fragment);
                if(isValidPassword(passwordEditText.getText())){
                    //create encrypted database and set password of user choice
                    //TODO
                    //change fragment to next fragment
                    SecondFragment secondFragment=new SecondFragment();
                    mFragmentTransaction.replace(R.id.first_fragment,secondFragment);
                    mFragmentTransaction.commit();
                    //set fragment number to 1
                    mFragmentNumber=1;

                }else{
                    passwordEditText.setError("password length should be greater than 3");
                }
                break;
            case 1:
                passwordEditText=(EditText)findViewById(R.id.input_password_second_fragment);
                EditText emailEditText=(EditText)findViewById(R.id.input_email_second_fragment);
                if(!isValidEmail(emailEditText.getText())){
                    emailEditText.setError("please enter valid email address");
                    return;
                }
                if(!isValidPassword(passwordEditText.getText())){
                    passwordEditText.setError("password length should be greater than 3");
                    return;
                }
                //generate keys
                //TODO


        }
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
}
