package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.content.ReceiverCallNotAllowedException;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by tripleheader on 12/14/16.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder>{
    private HashMap<Integer,String> mNumberOfFiles=new HashMap<>();
    private HashMap<Integer,String> mFolderSizes=new HashMap<>();
    private HashMap<Integer,String> mFoldersEncryptionStatus=new HashMap<>();
    private ArrayList<Integer> mFileIndices=new ArrayList<>();
    private List<String> mAdapter	=new ArrayList<>();

    private Context                 mContext;
    private LayoutInflater          mInflator;
    private	ViewHolder 			    mViewHodler;

        public FileListAdapter(Context context){
        mInflator=(LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.mContext=context;
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
        TextView textView=holder.mTextView;
        textView.setText(mAdapter.get(position));
        ImageView google
    }

    @Override
        public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mAdapter.size();
    }


    private boolean isFileIndex(int index) {
        for (Integer i:
                mFileIndices) {
            if(i==index){
                return true;
            }
        }
        return false;
    }
    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    public void fillAdapter(String dirPath){
        //clear the adapter
        mAdapter.clear();
        mFoldersEncryptionStatus.clear();
        mNumberOfFiles.clear();
        mFolderSizes.clear();
        mFileIndices.clear();

        File file=new File(dirPath);
        if(file.isFile()){
            Log.d("google1","Filename is: "+file.getName());
            return;
        }
        File[] files=file.listFiles();
        // keep track of the index of a file not a folder
        int index=0;
        for (File f:
                files) {
            mAdapter.add(f.getName());
            if(f.isDirectory()){
                //file number of files adapter
                fillNumberofFiles(f,index++);
            }else{
                Log.d("google","Filename is: "+f.getName());
                mFileIndices.add(index);
                fillDataWithFile(file,index++);
            }

        }
    }

    private void fillDataWithFile(File file, int position) {
        mNumberOfFiles.put(position,""+getFileExtension(file));
        mFolderSizes.put(position,""+round((file.length()/1024f)/1024f,2)+"MBs");
        mFoldersEncryptionStatus.put(position,isEncryptedFolder(file));
    }

    private void fillNumberofFiles(File file,int folderPostion){
        mNumberOfFiles.put(folderPostion,""+file.listFiles().length +" items");
        mFolderSizes.put(folderPostion,""+round((getFolderSize(file)/1024f)/1024f,2)+"MBs");
        mFoldersEncryptionStatus.put(folderPostion,isEncryptedFolder(file));

    }
    private String getFileExtension(File f){
        if(!f.getName().contains(".")){
            return "file";
        }
        String name=f.getName().substring(0,f.getName().length()-3);

        if(f.getName().contains("pgp")){
            return name.substring(name.lastIndexOf('.'),name.length());
        }else{
            return name.substring(name.lastIndexOf('.'),name.length());

        }
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public TextView mNumberFilesTextView;
        public TextView mFolderSizeTextView;
        public TextView mEncryptionSatusTextView;
        public ViewHolder(View itemView){
                super(itemView);

            mViewHodler.mTextView=(TextView)itemView.findViewById(R.id.list_textview);
            mViewHodler.mImageView=(ImageView)itemView.findViewById(R.id.list_imageview);
            mViewHodler.mNumberFilesTextView=(TextView)itemView.findViewById(R.id.nofiles_textview);
            mViewHodler.mFolderSizeTextView=(TextView)itemView.findViewById(R.id.folder_size_textview);
            mViewHodler.mEncryptionSatusTextView=
                    (TextView)itemView.findViewById(R.id.encryption_status_textview);


        }

    }
}
