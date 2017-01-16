package com.cryptopaths.cryptofm.startup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.utils.ActionHandler;

/**
 * Created by tripleheader on 1/16/17.
 * activity
 */

public class KeySelectActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_select);
        setResult(RESULT_OK);
    }
    @ActionHandler(layoutResource = R.id.button_letsgo_keys_select)
    public void onUnlockButtonClick(View view) {
    }
}
