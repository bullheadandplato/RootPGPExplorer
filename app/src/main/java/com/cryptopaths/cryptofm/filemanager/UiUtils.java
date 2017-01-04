package com.cryptopaths.cryptofm.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.view.ActionMode;
import android.view.View;
import android.widget.Button;

import com.cryptopaths.cryptofm.R;

import static com.cryptopaths.cryptofm.filemanager.FileBrowserActivity.mFilesData;

/**
 * Created by home on 12/29/16.
 * user interface utilities
 * like creating dialog, reloading adapter etc
 */

public class UiUtils {
    static ActionMode actionMode;
    static Dialog createDialog(Context context, String title, String buttonTitle){
        final Dialog dialog=new Dialog(context);
        dialog.setTitle(title);
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

    public static void reloadData(Context context, FileListAdapter adapter){
        if(actionMode!=null){
            actionMode.finish();
        }
        String path=adapter.getmFile().getCurrentPath();
        mFilesData.remove(path);
        FileFillerWrapper temp=new FileFillerWrapper(path,context);
        mFilesData.put(path,temp);
        adapter.setmFile(temp);
        adapter.notifyDataSetChanged();

    }
}
