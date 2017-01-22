package com.cryptopaths.cryptofm.filemanager.listview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;


/**
 * Created by tripleheader on 12/14/16.
 *
 *
 */

public class FileListAdapter extends RecyclerView.Adapter<ViewHolder>{


    private  static final String    TAG                 = "filesList";
    private static final int        NORMAL_VIEW         = 50;
    private Context mContext;
    private FileFillerWrapper mFileFiller;
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

    public void setmFileFiller(FileFillerWrapper mFileFiller) {
        this.mFileFiller = mFileFiller;
    }

    @Override
    public int getItemViewType(int position) {
            return NORMAL_VIEW;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
             DataModelFiles mDataModel= mFileFiller.getFileAtPosition(position);
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
        return mFileFiller.getTotalFilesCount();
    }

}
