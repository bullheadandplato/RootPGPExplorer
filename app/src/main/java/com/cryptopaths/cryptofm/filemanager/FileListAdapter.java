package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by tripleheader on 12/14/16.
 *
 *
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder>{

    private Context             mContext;
    private LayoutInflater      mInflator;
    private	ViewHolder 			mViewHodler;
    private FileFillerWrapper   mFile;
    private Drawable            mSelectedFileIcon;
    private Drawable            mFileIcon;
    private Drawable            mFolderIcon;
    private DataModelFiles      mDataModel;

    private Boolean  mSelectionMode                 = false;
    private  static final String TAG                = "filesList";
    private static final int NO_FILES_VIEW          = 100;
    private static final int NORMAL_VIEW            = 50;
    private ArrayList<Integer> mSelectedPosition    = new ArrayList<>();
    private ArrayList<String> mSelectedFilePaths    = new ArrayList<>();


        public FileListAdapter(Context context){
        mInflator=(LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.mContext=context;
            mSelectedFileIcon = mContext.getDrawable(R.drawable.ic_check_circle_white_48dp);
            mFileIcon         = mContext.getDrawable(R.drawable.ic_insert_drive_file_white_48dp);
            mFolderIcon       = mContext.getDrawable(R.drawable.ic_folder_white_48dp);

    }

    public void setmFile(FileFillerWrapper mFile) {
        this.mFile = mFile;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==NO_FILES_VIEW){
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.no_files_layout,parent,false));
        }else{
            Context context=parent.getContext();
            LayoutInflater inflater=LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.filebrowse_lisrview,parent,false);
            return new ViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(mFile.getTotalFilesCount()<1){
            return NO_FILES_VIEW;
        }else{
            return NORMAL_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mFile.getTotalFilesCount()<1){
            Log.d("nofiles","Yes there are no files here");
            ImageView view=holder.noFilesLayout;
            view.setImageDrawable(mContext.getDrawable(R.drawable.nofiles_image));
        }else {
            mDataModel=mFile.getFileAtPosition(position);
            TextView textView1=holder.mTextView;
            ImageView imageView=holder.mImageView;
            TextView textView2=holder.mFolderSizeTextView;
            TextView textView3=holder.mEncryptionSatusTextView;
            TextView textView4=holder.mNumberFilesTextView;

            textView1.setText(mDataModel.getFileName());
            textView2.setText(mDataModel.getFileSize());
            textView3.setText(mDataModel.getFileEncryptionStatus());
            textView4.setText(mDataModel.getFileExtension());
            imageView.setImageDrawable(mDataModel.getFileIcon());
        }

    }

    @Override
        public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if(mFile.getTotalFilesCount()<1){
            return 1;
        }
        return mFile.getTotalFilesCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public ImageView noFilesLayout;
        public TextView mTextView;
        public TextView mNumberFilesTextView;
        public TextView mFolderSizeTextView;
        public TextView mEncryptionSatusTextView;
        LongClickCallBack clickCallBack;
        public ViewHolder(View itemView){
                super(itemView);
            clickCallBack=(LongClickCallBack)mContext;
            itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mFile.getTotalFilesCount()<1){
                            return;
                        }
                        if(mSelectionMode){
                            clickCallBack.incrementSelectionCount();
                            selectionOperation(getAdapterPosition());
                            return;
                        }
                        TextView textView=(TextView)view.findViewById(R.id.list_textview);
                        String filename=textView.getText().toString();
                        if(FileUtils.isFile(filename)){
                            //open file TODO
                            Toast.makeText(mContext, "You click at file: "+filename, Toast.LENGTH_SHORT).show();
                        }else{
                            //check if folder already visited
                            String folderPath=mFile.getCurrentPath()+filename+"/";
                            if(FileBrowserActivity.mFilesData.containsKey(folderPath)){
                                Log.d(TAG, "onClick: filepath is and yes: "+folderPath);
                                mFile= FileBrowserActivity.mFilesData.get(folderPath);
                                FileUtils.CURRENT_PATH=folderPath;
                                notifyDataSetChanged();
                            }else{
                                //first visit folder
                                Log.d(TAG, "onClick: filepath is: "+folderPath);
                                mFile=new FileFillerWrapper(folderPath,mContext);
                                FileBrowserActivity.mFilesData.put(folderPath,mFile);
                                notifyDataSetChanged();

                            }
                        }
                    }
                });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mFile.getTotalFilesCount()<1){
                        return false;
                    }
                    Log.d("menu","yes im in Onlongclick");
                    clickCallBack.onLongClick();
                    mSelectionMode=true;
                    selectionOperation(getAdapterPosition());
                    return true;
                }
            });
            mTextView=(TextView)itemView.findViewById(R.id.list_textview);
            mImageView=(ImageView)itemView.findViewById(R.id.list_imageview);
            mNumberFilesTextView=(TextView)itemView.findViewById(R.id.nofiles_textview);
            mFolderSizeTextView=(TextView)itemView.findViewById(R.id.folder_size_textview);
            mEncryptionSatusTextView=
                    (TextView)itemView.findViewById(R.id.encryption_status_textview);
            noFilesLayout=(ImageView) itemView.findViewById(R.id.no_files_image);



        }

    }
    private void selectionOperation(int position){
        mDataModel  = mFile.getFileAtPosition(position);
        if(mDataModel.getSelected()){
            mDataModel.setSelected(false);
            mSelectedPosition.remove((Object)position);
            mSelectedFilePaths.remove(mDataModel.getFilePath());
            if(mDataModel.getFile()){
                mDataModel.setFileIcon(mFileIcon);
            }else{
                mDataModel.setFileIcon(mFolderIcon);
            }

        }else{
            mSelectedPosition.add(position);
            mSelectedFilePaths.add(mDataModel.getFilePath());
            mDataModel.setFileIcon(mSelectedFileIcon);
            mDataModel.setSelected(true);
        }

        notifyItemChanged(position);

    }

    public interface LongClickCallBack{
        public void onLongClick();
        public void incrementSelectionCount();
    }
    public void setmSelectionMode(Boolean value){
        //first check if there are select files
        if(mSelectedPosition.size()>0){
            //to avoid concurrent exception
            ArrayList<Integer> tmp=(ArrayList<Integer>) mSelectedPosition.clone();
            //remove the selection.
            for (Integer position:
                 tmp) {
                selectionOperation(position);
            }
        }
        this.mSelectionMode=value;
    }

    public ArrayList<String> getmSelectedFilePaths() {
        return mSelectedFilePaths;
    }
}
