/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.about;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.slownet5.pgprootexplorer.R;



public class LicencesActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licences);
        findViewById(R.id.spongy_castle_license).setOnClickListener(this);
        findViewById(R.id.easy_permission_license).setOnClickListener(this);
        findViewById(R.id.sqlcipher_license).setOnClickListener(this);
        findViewById(R.id.numix_icon_license).setOnClickListener(this);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Licences");
        }
    }


    @Override
    public void onClick(View view) {
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
