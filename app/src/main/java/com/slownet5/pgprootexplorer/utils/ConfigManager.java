package com.slownet5.pgprootexplorer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.slownet5.pgprootexplorer.CryptoFM;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;

/**
 * Created by bullhead on 5/29/17.
 *
 */

public final class ConfigManager {
    public static final String ASK_ENCRYPTION  = "askenc";
    public static final String ASK_DEL         = "askdel";
    public static final String ASK_KEY_PASS    = "passask";

    public static void loadCurrentConfig(){
        SharedPreferences prefs=CryptoFM.getContext()
                .getSharedPreferences(CommonConstants.COMMON_SHARED_PEREFS_NAME, Context.MODE_PRIVATE);

        SharedData.ASK_ENCRYPTION_CONFIG    = prefs.getBoolean(ASK_ENCRYPTION,false);
        SharedData.ASK_DEL_AFTER_ENCRYPTION = prefs.getBoolean(ASK_DEL,false);
        SharedData.ASK_KEY_PASSS_CONFIG     = prefs.getBoolean(ASK_KEY_PASS,false);
    }
    public static void saveConfig(String config,boolean value){
        SharedPreferences.Editor editor=CryptoFM.getContext()
                .getSharedPreferences(CommonConstants.COMMON_SHARED_PEREFS_NAME,Context.MODE_PRIVATE).edit();
        editor.putBoolean(config,value);
        editor.apply();
        editor.commit();
    }
}
