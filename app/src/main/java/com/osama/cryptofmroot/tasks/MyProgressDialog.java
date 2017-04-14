/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofmroot.tasks;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.utils.SharedData;

/**
 * Created by tripleheader on 1/8/17.
 * custom progress viewer for all the tasks
 */

public class MyProgressDialog {
    private Dialog                      dialog;
    private TextView                    mProgressTextView;
    private Context                     mContext;
    private String                      mContentTitle;
    private String                      mCurrentFilename;
    private Boolean                     isInNotificationMode;
    private NotificationManager         mNotificationManager;
    private NotificationCompat.Builder  mNotBuilder;
    private static int                  NOTIFICATION_NUMBER=0;
    private int                         thisNotificationNumber=0;
    private ProgressBar                 mProgressBar;

    private static final String TAG=MyProgressDialog.class.getName();

    MyProgressDialog(Context context, String title, final AsyncTask task){
        dialog=new Dialog(context);
        Log.d("dialog", "MyProgressDialog: not cancelable");
        dialog.setCanceledOnTouchOutside(false);
        this.mContext=context;
        this.mContentTitle=title;
        this.isInNotificationMode=false;
        dialog.setContentView(R.layout.task_progress_layout);
        ((TextView)dialog.findViewById(R.id.progress_dialog_title)).setText(title);
        mProgressTextView=((TextView)dialog.findViewById(R.id.filename_progress_textview));
        mProgressBar= (ProgressBar) dialog.findViewById(R.id.dialog_progressbar);
        mProgressBar.setMax(100);
        dialog.findViewById(R.id.runin_background_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                buildNotification();
            }
        });
        dialog.findViewById(R.id.cancel_background_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("cancel","Canceling the task");
                SharedData.IS_TASK_CANCELED=true;
                task.cancel(true);
                dialog.dismiss();

            }
        });
    }

    private void buildNotification() {
        thisNotificationNumber  = NOTIFICATION_NUMBER++;
        mNotificationManager    = NotificationController.getNotificationManager();
        mNotBuilder             = new NotificationCompat.Builder(mContext);
        mNotBuilder.setContentTitle(mContentTitle);
        mNotBuilder.setContentText(mCurrentFilename);
        mNotBuilder.setSmallIcon(R.drawable.logofinal);
        Log.d("notification","yoo showing notification");
        mNotBuilder.setProgress(0,0,true);
        mNotBuilder.setOngoing(true);
        mNotificationManager.notify(thisNotificationNumber,mNotBuilder.build());
        isInNotificationMode    = true;

        //add notification
        NotificationController.addNewNotification(mNotBuilder,thisNotificationNumber);

    }

    public void setmProgressTextViewText(String text) {
        mCurrentFilename=text;
        if(isInNotificationMode){
             mNotBuilder.setContentText(text);
             mNotificationManager.notify(thisNotificationNumber,mNotBuilder.build());
        }else{
            this.mProgressTextView.setText(text);
        }
    }
    void dismiss(String text){
        if(isInNotificationMode){
            mNotBuilder.setContentText(text);
            mNotBuilder.setOngoing(false);
            mNotBuilder.setProgress(100,100,false);
            mNotificationManager.notify(thisNotificationNumber,mNotBuilder.build());
            this.mNotBuilder=null;
            NotificationController.removeNotification(thisNotificationNumber);
        }else{
            dialog.dismiss();
        }
    }
    void show(){
        dialog.show();
    }

    private int prevProgress=0;
    public void setProgress(final Integer integer) {
        if(isInNotificationMode && prevProgress<integer){
            prevProgress=integer;
            Log.d(TAG, "setProgress: integer is: "+integer);
            mNotBuilder.setProgress(100,integer,false);
            mNotificationManager.notify(thisNotificationNumber,mNotBuilder.build());

        }else{
            mProgressBar.setProgress(integer);
        }
    }

    public void setMessage(String s) {
        this.mProgressTextView.setText(s);
    }

    public void setIndeterminate(boolean b) {
        this.mProgressBar.setIndeterminate(b);
    }
    public boolean isInNotifyMode(){
        return isInNotificationMode;
    }
}
