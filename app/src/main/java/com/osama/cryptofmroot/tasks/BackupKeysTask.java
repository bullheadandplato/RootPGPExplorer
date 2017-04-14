package com.osama.cryptofmroot.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.encryption.DatabaseHandler;
import com.osama.cryptofmroot.filemanager.utils.SharedData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by bullhead on 4/2/17.
 *
 */

public class BackupKeysTask extends AsyncTask<String,Void,Boolean> {
    private static final String TAG=BackupKeysTask.class.getCanonicalName();
    private String errorMessage;
    @Override
    protected Boolean doInBackground(String... strings) {
        String pass=strings[0];


        try {
            DatabaseHandler handler=new DatabaseHandler(CryptoFM.getContext(),pass,true);
            byte[] key=handler.getSecretKeyFromDb(SharedData.USERNAME);
            //create directory
            File dir=new File(SharedData.FILES_ROOT_DIRECTORY+"CryptoFM");
            if(!dir.exists()){
                if(!dir.mkdir()){
                    errorMessage="Cannot create directory to store keys.";
                    return false;
                }
            }
            File secKey=new File(SharedData.FILES_ROOT_DIRECTORY+"CryptoFM/sec.key");
            if(!secKey.exists()){
                if(!secKey.createNewFile()){
                    errorMessage="Cannot create key file.";
                    return false;
                }
            }
            BufferedOutputStream out=new BufferedOutputStream(
                    new FileOutputStream(secKey));
            Log.d(TAG, "doInBackground: key is "+key);
            out.write(key);
            out.close();

            File pubKey=new File(CryptoFM.getContext().getFilesDir(),"pub.asc");

            File outPubKey=new File(SharedData.FILES_ROOT_DIRECTORY+"CryptoFM/pub.key");
            if(!outPubKey.exists()){
                if(!outPubKey.createNewFile()){
                    errorMessage="Cannot create key file.";
                    return false;
                }
            }
            BufferedInputStream in=new BufferedInputStream(new FileInputStream(pubKey));
            out=new BufferedOutputStream(
                    new FileOutputStream(outPubKey));
            byte[] data=new byte[2048];
            int pos;
            while((pos=in.read(data))>0){
                out.write(data,0,pos);
            }
            out.close();
        } catch (Exception e) {
            errorMessage=e.getMessage();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(aBoolean){
            Toast.makeText(CryptoFM.getContext(),
                    "Keys exported to folder \'cryptoFM\'",Toast.LENGTH_SHORT).show();
        }else {

            Toast.makeText(CryptoFM.getContext(),
                    "Cannot export keys. "+errorMessage,Toast.LENGTH_SHORT).show();
        }
    }
}
