package com.cryptopaths.cryptofm.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.cryptopaths.cryptofm.utils.FileUtils;

/**
 * Created by tripleheader on 1/4/17.
 * service to perform cleanups after application exit
 */

public class CleanupService  extends Service{
    public static Boolean IS_TASK_RUNNING=false;
    public static NotificationCompat.Builder NOTIFICATION_BUILDER=null;
    public static NotificationManager NOTIFICATION_MANAGER=null;
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
        if(NOTIFICATION_BUILDER!=null && !IS_TASK_RUNNING){
            NOTIFICATION_BUILDER.setContentText("Operation Canceled");
            NOTIFICATION_BUILDER.setOngoing(false);
            NOTIFICATION_BUILDER.setProgress(100,100,false);
            NOTIFICATION_MANAGER.notify(1,NOTIFICATION_BUILDER.build());
        }
        FileUtils.deleteDecryptedFolder();
        stopSelf();
    }
}
