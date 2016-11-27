package com.cryptopaths.cryptofm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {
    private TextView    emailTextView;
    private TextView    passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //map the references
        setVariablesReferences();
    }
    private void setVariablesReferences(){
        this.emailTextView      =(TextView)findViewById(R.id.input_email);
        this.passwordTextView   =(TextView)findViewById(R.id.input_password);
    }

    /**
     * when user clicks on generate keys button this method will be invoked. It is action method for
     * button. It start the process for generating keys.
     * @param v the view for which this function is handler.
     */
    @ActionHandler
    public void onGenerateKeysButtonClick(View v){
        //check if user enter required information correctly
       assert emailTextView!=null;
        assert passwordTextView!=null;
        if(!isValidEmail(emailTextView.getText())){
            emailTextView.setError("Please enter valid email");
            return;
        }
        if(!isValidPassword(passwordTextView.getText())){
            passwordTextView.setError("Password length should be greater than 4");
            return;
       }
        //start the key generation process
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
