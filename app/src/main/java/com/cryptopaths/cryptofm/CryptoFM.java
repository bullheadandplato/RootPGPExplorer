package com.cryptopaths.cryptofm;

import android.app.Application;
import android.content.Context;

/**
 * Created by tripleheader on 1/26/17.
 * Main application class
 */

public class CryptoFM extends Application {

    private static CryptoFM instance;

    public CryptoFM() {
        instance = this;
    }

    public static Context getContext() {
        if(instance==null){
            instance=new  CryptoFM();
        }
        return instance;
    }
}
