package com.osama.cryptofm.tasks;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.osama.cryptofm.CryptoFM;

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
}
