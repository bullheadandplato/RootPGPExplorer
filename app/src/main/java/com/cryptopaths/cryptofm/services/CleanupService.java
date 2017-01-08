package com.cryptopaths.cryptofm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cryptopaths.cryptofm.utils.FileUtils;

/**
 * Created by tripleheader on 1/4/17.
 * service to perform cleanups after application exit
 */

public class CleanupService  extends Service{
    public static Boolean IS_DECRYPTION_RUNNING=false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if(!IS_DECRYPTION_RUNNING){
            FileUtils.deleteDecryptedFolder();
        }
        stopSelf();
    }
}
