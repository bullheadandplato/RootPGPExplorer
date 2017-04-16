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
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;

import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.listview.FileListAdapter;


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
            SharedData.DO_NOT_RESET_ICON=true;
            actionMode.finish();
        }
        Log.d(TAG, "reloadData: Happy here");
        String path= adapter.getmFileFiller().getCurrentPath();
        adapter.getmFileFiller().fillData(path,adapter);
    }
}
