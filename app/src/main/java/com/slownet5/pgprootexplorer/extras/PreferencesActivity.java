package com.slownet5.pgprootexplorer.extras;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;


import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.utils.ConfigManager;
import com.slownet5.pgprootexplorer.utils.MainUtils;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        MainUtils.closeActionBarButton(this);

        //set click listeners
        findViewById(R.id.prefs_askenc_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnyBoxClick(v);
            }
        });
        findViewById(R.id.prefs_autodel_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnyBoxClick(v);
            }
        });
        findViewById(R.id.prefs_savepass_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnyBoxClick(v);
            }
        });
    }
    private void onAnyBoxClick(View view){
        int id=view.getId();
        boolean value=((CheckBox)view).isChecked();
        String config=null;
        switch(id){
            case R.id.prefs_askenc_box: config=ConfigManager.ASK_ENCRYPTION; break;
            case R.id.prefs_autodel_box: config=ConfigManager.ASK_DEL;break;
            case R.id.prefs_savepass_box: config=ConfigManager.ASK_KEY_PASS;break;
        }
        ConfigManager.saveConfig(config,value);
    }
}
