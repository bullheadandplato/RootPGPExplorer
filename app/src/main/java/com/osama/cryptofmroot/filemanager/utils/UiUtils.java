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

package com.osama.cryptofmroot.filemanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.extras.TextEditorActivity;
import com.osama.cryptofmroot.filemanager.listview.FileListAdapter;
import com.osama.cryptofmroot.root.RootUtils;
import com.osama.cryptofmroot.utils.CommonConstants;
import com.osama.cryptofmroot.utils.FileUtils;


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
            actionMode.finish();
        }else {
           refill(adapter);
        }
    }
    public static void refill(FileListAdapter adapter){
        Log.d(TAG, "reloadData: Happy here");
            String path = adapter.getmFileFiller().getCurrentPath();
            adapter.getmFileFiller().fillData(path, adapter);
    }

    public static void openFile(String filename,Context ctx){
          if(RootUtils.isRootPath(filename)){
                    /*String sPath=CryptoFM.getContext().getExternalCacheDir().getAbsolutePath()+"/"+new File(filename).getName();
                    Shell.SU.run("cat '"+filename+"' > '"+ sPath+"'");
                    filename=sPath;*/
                    RootUtils.chmod666(filename);
                }
         String mimeType =
                    MimeTypeMap.getSingleton().
                            getMimeTypeFromExtension(
                                    FileUtils.getExtension(filename
                                    )
                            );

            Intent intent = new Intent();
        Uri uri=FileUtils.getUri(filename);

                intent.setDataAndType(uri, mimeType);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            Intent x = Intent.createChooser(intent, "Open with: ");
            if(intent.resolveActivity(ctx.getPackageManager())!=null){
                ctx.startActivity(x);
            }else{
                openWith(filename,ctx);
            }
        }
        public static void openWith(final String filename,final Context ctx){

        Log.d("nullpov", "openWith: filename is: "+filename);
        final Dialog dialog=new Dialog(ctx);
        dialog.setContentView(R.layout.openwith_dialog_layout);
        dialog.findViewById(R.id.openwith_texteditor_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(ctx, TextEditorActivity.class);
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

}


