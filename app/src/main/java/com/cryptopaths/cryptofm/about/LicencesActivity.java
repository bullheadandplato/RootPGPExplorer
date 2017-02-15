package com.cryptopaths.cryptofm.about;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cryptopaths.cryptofm.R;



public class LicencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licences);
    }
    public void licenseClick(View view){
        Intent intent=new Intent(this,ShowLicenseActivity.class);
        String key="license";
        switch (view.getId()){
            case R.id.spongy_castle_license:{
                intent.putExtra(key,getString(R.string.spongylicense));
                break;
            }
            case R.id.easy_permission_license:{
                intent.putExtra(key,getString(R.string.easy_permission_license));
                break;
            }
            case R.id.sqlcipher_license:{
                intent.putExtra(key,getString(R.string.sqlcipher_license));
                break;
            }
            case R.id.numix_icon_license:{
                intent.putExtra(key,getString(R.string.numix_icon_license));
                break;
            }
        }
        startActivity(intent);
    }
}
