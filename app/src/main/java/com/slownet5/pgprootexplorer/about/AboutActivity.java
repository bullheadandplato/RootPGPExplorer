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

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViewById(R.id.credit).setOnClickListener(this);
        findViewById(R.id.team).setOnClickListener(this);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("About");
        }
    }
    public void onLicenseClick(View view){


    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.credit:
                startActivity(new Intent(this,LicencesActivity.class));
                break;
            case R.id.team:
                startActivity(new Intent(this,ShowTeamActivity.class));
                break;
        }
    }
}
