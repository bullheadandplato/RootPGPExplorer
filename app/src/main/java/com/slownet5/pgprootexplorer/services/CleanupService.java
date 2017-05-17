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

package com.slownet5.pgprootexplorer.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.slownet5.pgprootexplorer.tasks.NotificationController;
import com.slownet5.pgprootexplorer.utils.FileUtils;

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
