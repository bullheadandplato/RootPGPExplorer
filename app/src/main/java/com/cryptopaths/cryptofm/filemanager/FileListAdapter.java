package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by tripleheader on 12/14/16.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder>{

    private Context                 mContext;
    private LayoutInflater          mInflator;
    private	ViewHolder 			    mViewHodler;
    private FileFillerWrapper       mFile;
    private  static final String TAG="filesList";

        public FileListAdapter(Context context){
        mInflator=(LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.mContext=context;

    }

    public void setmFile(FileFillerWrapper mFile) {
        this.mFile = mFile;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.filebrowse_lisrview,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataModelFiles temp=mFile.getFileAtPosition(position);
        TextView textView1=holder.mTextView;
        ImageView imageView=holder.mImageView;
        TextView textView2=holder.mFolderSizeTextView;
        TextView textView3=holder.mEncryptionSatusTextView;
        TextView textView4=holder.mNumberFilesTextView;

        textView1.setText(temp.getFileName());
        textView2.setText(temp.getFileSize());
        textView3.setText(temp.getFileEncryptionStatus());
        textView4.setText(temp.getFileExtension());
        imageView.setImageDrawable(temp.getFileIcon());
    }

    @Override
        public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mFile.getTotalFilesCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public TextView mNumberFilesTextView;
        public TextView mFolderSizeTextView;
        public TextView mEncryptionSatusTextView;
        public ViewHolder(View itemView){
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView textView=(TextView)view.findViewById(R.id.list_textview);
                        String filename=textView.getText().toString();
                        if(FileUtils.isFile(filename)){
                            //open file TODO
                        }else{
                            //check if folder already visited
                            String folderPath=mFile.getCurrentPath()+filename;
                            if(FileBrowserActivity.mFilesData.containsKey(folderPath)){
                                Log.d(TAG, "onClick: filepath is and yes: "+folderPath);
                                mFile= FileBrowserActivity.mFilesData.get(filename);
                            }else{
                                //first visit folder
                                Log.d(TAG, "onClick: filepath is: "+folderPath);
                                mFile=new FileFillerWrapper(folderPath,mContext);
                                FileBrowserActivity.mFilesData.put(filename,mFile);
                                notifyDataSetChanged();

                            }
                        }
                    }
                });
            mTextView=(TextView)itemView.findViewById(R.id.list_textview);
            mImageView=(ImageView)itemView.findViewById(R.id.list_imageview);
            mNumberFilesTextView=(TextView)itemView.findViewById(R.id.nofiles_textview);
            mFolderSizeTextView=(TextView)itemView.findViewById(R.id.folder_size_textview);
            mEncryptionSatusTextView=
                    (TextView)itemView.findViewById(R.id.encryption_status_textview);


        }

    }

}
