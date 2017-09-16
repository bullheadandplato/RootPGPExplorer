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

package com.slownet5.pgprootexplorer.filemanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.extras.TextEditorActivity;
import com.slownet5.pgprootexplorer.filemanager.listview.FileListAdapter;
import com.slownet5.pgprootexplorer.root.RootUtils;
import com.slownet5.pgprootexplorer.tasks.CompressTask;
import com.slownet5.pgprootexplorer.utils.CommonConstants;
import com.slownet5.pgprootexplorer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by home on 12/29/16.
 * user interface utilities
 * like creating dialog, reloading adapter etc
 */

public class UiUtils {
    public static ActionMode actionMode;

    private static final String TAG=UiUtils.class.getCanonicalName();

    public static Dialog createDialog(Context context, String title, String buttonTitle){
        final Dialog dialog=new Dialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.create_file_dialog);
        dialog.show();
        ((Button) dialog.findViewById(R.id.create_file_button)).setText(buttonTitle);

        dialog.findViewById(R.id.cancel_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static void reloadData(FileListAdapter adapter){

        SharedData.CURRENT_RUNNING_OPERATIONS.clear();
        if(actionMode!=null){
            Log.d(TAG, "reloadData: finish adapter");
            actionMode.finish();
            actionMode=null;
        }else {
           refill(adapter);
        }
    }
    public static void refill(FileListAdapter adapter){
        Log.d(TAG, "reloadData: Happy here");
            String path = adapter.getmFileFiller().getCurrentPath();
            adapter.getmFileFiller().fillData(path, adapter);
    }

    public static void openFile(String filename,Context ctx,FileListAdapter adapter){
          if(RootUtils.isRootPath(filename)){
                    /*String sPath=CryptoFM.getContext().getExternalCacheDir().getAbsolutePath()+"/"+new File(filename).getName();
                    Shell.SU.run("cat '"+filename+"' > '"+ sPath+"'");
                    filename=sPath;*/
                    RootUtils.chmod666(filename);
                }
                if(FileUtils.getExtension(filename).equalsIgnoreCase("zip")){
                    openZipFile(filename,ctx,adapter);
                    return;
                }
         startActivity(Intent.ACTION_VIEW,filename,ctx);

        }

    private static void openZipFile(final String filename,Context ctx,FileListAdapter adapter) {
        ArrayList<String> tmp=new ArrayList<>();
        tmp.add(filename);
        new CompressTask(tmp,ctx,adapter,true,adapter.getmFileFiller().getCurrentPath()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void openWith(final String filename,final Context ctx){

        final Dialog dialog=new Dialog(ctx);
        dialog.setContentView(R.layout.openwith_dialog_layout);
        dialog.findViewById(R.id.openwith_texteditor_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(ctx, TextEditorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(CommonConstants.TEXTEDITACT_PARAM_PATH,filename);
                ctx.startActivity(intent);
            }
        });
        dialog.findViewById(R.id.openwith_installed_app_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public static void shareFile(String s,Context ctx) {
        if(RootUtils.isRootPath(s)){
            try {
                File f=FileUtils.getFile(SharedData.CRYPTO_FM_PATH+"share");
                if (!f.exists()){
                    f.mkdirs();
                }
                RootUtils.copyFile(s,f.getAbsolutePath());
                s=f.getAbsolutePath()+s;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        startActivity(Intent.ACTION_SEND,s,ctx);
    }

    private static void startActivity(String action,String filename,Context ctx){
        Uri uri=FileUtils.getUri(filename);
        Intent intent=new Intent();
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(action);
        intent.setDataAndType(uri,FileUtils.getMimeType(filename));
        Intent chooser;
        if(action.equals(Intent.ACTION_SEND)){
            chooser=Intent.createChooser(intent,"Sharing....");
            if(intent.resolveActivity(ctx.getPackageManager())!=null) {
                ctx.startActivity(chooser);
            }
        }else{
            chooser=Intent.createChooser(intent,"Open with....");
            if(intent.resolveActivity(ctx.getPackageManager())!=null){
                ctx.startActivity(chooser);
            }else{
                openWith(filename,ctx);
            }
        }
    }
}


