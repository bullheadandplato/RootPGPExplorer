package com.osama.cryptofmroot.extras;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.encryption.DatabaseHandler;
import com.osama.cryptofmroot.encryption.MyPGPUtil;
import com.osama.cryptofmroot.encryption.PGPUtil;
import com.osama.cryptofmroot.filemanager.utils.SharedData;

import net.sqlcipher.database.SQLiteDatabase;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPSecretKey;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class KeyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_details);
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
