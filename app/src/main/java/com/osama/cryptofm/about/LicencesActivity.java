/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofm.about;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.osama.cryptofm.R;



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
