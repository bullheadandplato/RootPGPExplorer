package com.cryptopaths.cryptofm.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.listview.FileFillerWrapper;
import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;

//import static com.cryptopaths.cryptofm.filemanager.ui.FileBrowserActivity.mFilesData;

/**
 * Created by home on 12/29/16.
 * user interface utilities
 * like creating dialog, reloading adapter etc
 */

public class UiUtils {
    public static ActionMode actionMode;

    public static Dialog createDialog(Context context, String title, String buttonTitle){
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
        String path= adapter.getmFileFiller().getCurrentPath();
        adapter.getmFileFiller().fillData(path,context);
        adapter.notifyDataSetChanged();

    }
}
