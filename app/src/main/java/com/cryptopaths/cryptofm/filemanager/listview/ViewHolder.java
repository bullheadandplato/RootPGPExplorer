package com.cryptopaths.cryptofm.filemanager.listview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptofm.utils.FileUtils;

/**
 * Created by tripleheader on 1/21/17
 * View holder
 */

class ViewHolder extends RecyclerView.ViewHolder{
    private FileSelectionManagement mFileSelectionManagement;
    private static final String TAG="ViewHolder";
    ImageView        mImageView;
    TextView         mTextView;
    TextView         mNumberFilesTextView;
    TextView         mFolderSizeTextView;
    ImageView        mEncryptionStatusImage;


    ViewHolder(View itemView, Context c,FileSelectionManagement m){
        super(itemView);
        mFileSelectionManagement= m;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SharedData.SELECTION_MODE){
                    mFileSelectionManagement.selectionOperation(getAdapterPosition());
                    return;
                }
                TextView textView   = (TextView)view.findViewById(R.id.list_textview);
                String filename     = textView.getText().toString();
                if(FileUtils.isFile(filename)){
                    if(SharedData.STARTED_IN_SELECTION_MODE){
                        Log.d(TAG, "onClick: yes nigga im started in selection mode");
                        mFileSelectionManagement.selectFileInSelectionMode(getAdapterPosition());
                        return;
                    }
                        mFileSelectionManagement.openFile(filename);
                }else{
                        mFileSelectionManagement.openFolder(filename,getAdapterPosition());
                    }

            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(SharedData.STARTED_IN_SELECTION_MODE){
                    return false;
                }
                else if(!SharedData.SELECTION_MODE) {
                    Log.d(TAG, "onLongClick: action mode is not being displayed");
                    mFileSelectionManagement.startSelectionMode();
                }
                mFileSelectionManagement.selectionOperation(getAdapterPosition());
                return true;

            }
        });

        mTextView                   = (TextView)itemView.findViewById(R.id.list_textview);
        mImageView                  = (ImageView)itemView.findViewById(R.id.list_imageview);
        mNumberFilesTextView        = (TextView)itemView.findViewById(R.id.nofiles_textview);
        mFolderSizeTextView         = (TextView)itemView.findViewById(R.id.folder_size_textview);
        mEncryptionStatusImage      = (ImageView) itemView.findViewById(R.id.encryption_status_image);

    }

}
