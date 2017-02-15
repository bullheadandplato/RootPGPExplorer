package com.osama.cryptofm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.osama.cryptofm.tasks.NotificationController;
import com.osama.cryptofm.utils.FileUtils;

/**
 * Created by tripleheader on 1/4/17.
 * service to perform cleanups after application exit
 */

public class CleanupService  extends Service{
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
        if(NotificationController.getNotifications()!=null && NotificationController.getNotifications().size()>0){
            for (NotificationController notfication:NotificationController.getNotifications()) {
                notfication.getNotificationCompat().setProgress(100,100,false);
                notfication.getNotificationCompat().setOngoing(false);
                notfication.getNotificationCompat().setContentText("Operation canceled");
                notfication.getNotificationCompat().setSubText("Application might be closed!!");
                NotificationController.getNotificationManager().notify(notfication.getNotificationNumber(),notfication.getNotificationCompat().build());
            }
        }
        FileUtils.deleteDecryptedFolder();
        stopSelf();
    }
}
