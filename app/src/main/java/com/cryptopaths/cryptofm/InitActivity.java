package com.cryptopaths.cryptofm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;


public class InitActivity extends AppCompatActivity {
    private int mFragmentNumber=0;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        mProgressBar=(ProgressBar)findViewById(R.id.setup_progressbar);
        mProgressBar.setMax(100);

    }
    @ActionHandler
    public void onNextButtonClick(View v){
        switch (mFragmentNumber){
            case 0:
                EditText passwordEditText=
                        (EditText)findViewById(R.id.input_password_first_fragment);
                CharSequence sequence=passwordEditText.getText();
                if(isValidPassword(sequence)){
                    Log.d("fragment","replacing fragmnet "+mFragmentNumber);
                    //create encrypted database and set password of user choice
                    //TODO
                    //change fragment to next fragment
                    SecondFragment secondFragment=new SecondFragment();
                    getSupportFragmentManager().beginTransaction().
                            setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
                                    R.anim.enter_from_left, R.anim.exit_to_right).
                            replace(R.id.first_fragment,secondFragment).
                            commit();
                    //set fragment number to 1
                    mFragmentNumber=1;
                    //set progress
                    mProgressBar.setProgress(33);

                }else{
                    passwordEditText.setError("password length should be greater than 3");
                }
                Log.d("fragment","replacing fragmnet "+mFragmentNumber);
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
                //change fragment to third fragment
                ThirdFragment thirdFragment=new ThirdFragment();
                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
                                R.anim.enter_from_left, R.anim.exit_to_right).
                        replace(R.id.first_fragment,thirdFragment).
                        commit();
                mFragmentNumber=2;
                mProgressBar.setProgress(66);
                break;
            case 2:
                //choose dir and start encrypting it
                //TODO
                //change the button text to lets go
                ((AppCompatButton) v).setText("Let's Go");
                mFragmentNumber=3;
                mProgressBar.setProgress(100);
                break;
            case 3:
                //start the encrypting activity
                //TODO
                break;




        }
    }
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private boolean isValidPassword(CharSequence password){
        return password.length() > 2;
    }
}
