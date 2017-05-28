package com.slownet5.pgprootexplorer.extras;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.listview.DataModelFiles;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.root.RootUtils;
import com.slownet5.pgprootexplorer.utils.FileUtils;

/**
 * Created by bullhead on 5/28/17.
 *
 */

public class FileInfoDialog {


    public static void show(Context ctx){
        Dialog dialog=new Dialog(ctx);
        dialog.setContentView(R.layout.activity_file_info);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        init(dialog);
        dialog.show();
    }
     private static void init(Dialog view){
        DataModelFiles files= SharedData.CURRENT_FILE_FOR_INFO;
        ((ImageView)view.findViewById(R.id.fileinfo_image)).setImageDrawable(files.getFileIcon());
        ((TextView)view.findViewById(R.id.fileinfo_name_textview)).setText(files.getFileName());
        String fileSize;
        String completeFilename=files.getFilePath()+files.getFileName();
        if(RootUtils.isRootPath(completeFilename)){
            fileSize= RootUtils.getFileSize(completeFilename);
        }else {
            fileSize=files.getFile() ? files.getFileDate() : FileUtils.getFolderSize(completeFilename);
        }
        ((TextView)view.findViewById(R.id.fileinfo_size_textview)).setText(fileSize);
        String tmp=files.isEncrypted() ? "Encrypted" : "Not encrypted";
        ((TextView)view.findViewById(R.id.fileinfo_permission_textview)).setText(tmp);
    }
}

