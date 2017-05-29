package com.slownet5.pgprootexplorer.extras;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.utils.MainUtils;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        MainUtils.closeActionBarButton(this);
    }
}
