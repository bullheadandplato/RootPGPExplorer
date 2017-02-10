package com.cryptopaths.cryptofm;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by tripleheader on 1/26/17.
 * Main application class
 */

public class CryptoFM extends Application {

    private static CryptoFM instance;
    private static final String TAG=CryptoFM.class.getCanonicalName();

    public CryptoFM() {
        if(instance!=null){
            throw new IllegalArgumentException("Instance is already created");
        }else{
            Log.i(TAG, "CryptoFM: Creating instance of class");
            instance = this;
        }
    }

    public static Context getContext() {
        if(instance==null){
            instance=new  CryptoFM();
        }
        return instance;
    }
}
