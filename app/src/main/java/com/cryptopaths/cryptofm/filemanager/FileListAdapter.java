package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by tripleheader on 12/14/16.
 *
 *
 */

public class FileListAdapter extends RecyclerView.Adapter<ViewHolder>{


    private  static final String    TAG                 = "filesList";
    private static final int        NORMAL_VIEW         = 50;
    private Context mContext;

    public FileListAdapter(Context context){
            this.mContext=context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context=parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view               = inflater.inflate(R.layout.filebrowse_card_view,parent,false);
            return new ViewHolder(view,mContext);

    }

    @Override
    public int getItemViewType(int position) {
            return NORMAL_VIEW;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
             DataModelFiles mDataModel=FileFillerWrapper.getFileAtPosition(position);
            TextView textView1=holder.mTextView;
            ImageView imageView=holder.mImageView;
            TextView textView2=holder.mFolderSizeTextView;
            ImageView image=holder.mEncryptionStatusImage;
            TextView textView4=holder.mNumberFilesTextView;

            textView1.setText(mDataModel.getFileName());
            textView2.setText(mDataModel.getFileSize());
            image.setImageDrawable(mDataModel.getFileEncryptionStatus());
            textView4.setText(mDataModel.getFileExtension());
            imageView.setImageDrawable(mDataModel.getFileIcon());

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return FileFillerWrapper.getTotalFilesCount();
    }

}
