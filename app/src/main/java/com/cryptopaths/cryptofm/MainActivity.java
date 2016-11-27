package com.cryptopaths.cryptofm;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    public void onGenerateKeysButtonClick(View v){
        //start the key generation process
        //TODO
    }

}
