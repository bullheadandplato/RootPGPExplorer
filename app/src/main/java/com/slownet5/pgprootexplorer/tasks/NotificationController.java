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

package com.slownet5.pgprootexplorer.tasks;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.slownet5.pgprootexplorer.CryptoFM;

import java.util.ArrayList;

/**
 * Created by home on 2/4/17.
 *
 */

public class NotificationController {
    private NotificationCompat.Builder notificationCompat;
    private static NotificationManager notificationManager=null;

    private int notificationNumber;
    private static ArrayList<NotificationController> allNotifications;

    public NotificationController(NotificationCompat.Builder build,int number){
        this.notificationCompat=build;
        this.notificationNumber=number;
    }

    public NotificationCompat.Builder getNotificationCompat() {
        return notificationCompat;
    }

    public static NotificationManager getNotificationManager() {
        if(notificationManager==null){
            notificationManager=
                    (NotificationManager) CryptoFM.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public int getNotificationNumber() {
        return notificationNumber;
    }
    public static void addNewNotification(NotificationCompat.Builder builder,int number){
        if(allNotifications==null){
            allNotifications=new ArrayList<>();
        }
        allNotifications.add(new NotificationController(builder,number));
    }
    public static ArrayList<NotificationController> getNotifications(){
        return allNotifications;
    }

    public static void removeNotification(int thisNotificationNumber) {
    allNotifications.remove(thisNotificationNumber);
    }
}
