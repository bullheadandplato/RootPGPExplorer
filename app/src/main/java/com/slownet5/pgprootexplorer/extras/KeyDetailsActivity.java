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

package com.slownet5.pgprootexplorer.extras;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.encryption.MyPGPUtil;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.filemanager.utils.UiUtils;
import com.slownet5.pgprootexplorer.utils.MainUtils;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;

import java.io.IOException;

public class KeyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_details);
        MainUtils.closeActionBarButton(this);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Key Details");
        }
        ((TextView)findViewById(R.id.key_details_name_textview)).setText(SharedData.USERNAME);
        fillKeyDetails();
    }

    private void fillKeyDetails() {
        if(!SharedData.KEYS_GENERATED){
            Snackbar.make(findViewById(R.id.key_details_keyid_textview),"Keys not generated.",Snackbar.LENGTH_SHORT).show();
            return;
        }
        try {
            PGPPublicKey key=MyPGPUtil.readPublicKey(getFilesDir()+"/"+"pub.asc");
            ((TextView)findViewById(R.id.key_details_keyid_textview)).setText(""+key.getKeyID());
            ((TextView)findViewById(R.id.key_details_keysize_textview)).setText(""+key.getBitStrength());
            if(key.getValidSeconds()==0){
                ((TextView)findViewById(R.id.key_details_keyalgo_textview)).setText("Key does not expire");
            }else{
                ((TextView)findViewById(R.id.key_details_keyalgo_textview)).setText(""+key.getValidSeconds());
            }
            ((TextView)findViewById(R.id.key_details_keytime_textview)).setText(""+key.getCreationTime());
        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }


}
