package com.slownet5.pgprootexplorer.extras;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;


import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.utils.ConfigManager;
import com.slownet5.pgprootexplorer.utils.MainUtils;

public class PreferencesActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        MainUtils.closeActionBarButton(this);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Preferences");
        }
        setBoxes();
    }

    private void setBoxes() {
        CheckBox box1=((CheckBox)findViewById(R.id.prefs_autodel_box));
        CheckBox box2=((CheckBox)findViewById(R.id.prefs_askenc_box));
        CheckBox box3=((CheckBox)findViewById(R.id.prefs_savepass_box));
        box1.setChecked(SharedData.ASK_DEL_AFTER_ENCRYPTION);
        box2.setChecked(SharedData.ASK_ENCRYPTION_CONFIG);
        box3.setChecked(SharedData.ASK_KEY_PASSS_CONFIG);
        box1.setOnClickListener(this);
        box2.setOnClickListener(this);
        box3.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home: finish(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view){
        int id=view.getId();
        boolean value=((CheckBox)view).isChecked();
        String config=null;
        switch(id){
            case R.id.prefs_askenc_box:      config=ConfigManager.ASK_ENCRYPTION; break;
            case R.id.prefs_autodel_box:     config=ConfigManager.ASK_DEL;break;
            case R.id.prefs_savepass_box:    config=ConfigManager.ASK_KEY_PASS;break;
        }
        ConfigManager.saveConfig(config,value);
    }
    @Override
    public void onPause(){
        ConfigManager.loadConfig();
        super.onPause();
    }
}
