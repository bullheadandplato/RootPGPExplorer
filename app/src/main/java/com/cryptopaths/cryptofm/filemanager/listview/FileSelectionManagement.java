package com.cryptopaths.cryptofm.filemanager.listview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.utils.MimeType;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.tasks.DecryptTask;
import com.cryptopaths.cryptofm.tasks.TasksFileUtils;
import com.cryptopaths.cryptofm.utils.FileDocumentUtils;
import com.cryptopaths.cryptofm.utils.FileUtils;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/21/17.
 * manage the selection of files
 * opening the files etc
 */

public class FileSelectionManagement {
    private DataModelFiles   mDataModel;
    private FileListAdapter  mFileListAdapter;
    private Context          mContext;
    private Drawable         mSelectedFileIcon;
    private Drawable         mFolderIcon;
    private AdapterCallbacks clickCallBack;

    private static final String TAG="FileSelectionManagement";


    private ArrayList<Integer>      mSelectedPosition   = new ArrayList<>();
    private ArrayList<String>       mSelectedFilePaths  = new ArrayList<>();
    private FileFillerWrapper       mFileFiller;

    public FileSelectionManagement(Context context, FileListAdapter adapter){
        this.mContext     = context;
        mSelectedFileIcon = mContext.getDrawable(R.drawable.ic_check_circle_white_48dp);
        mFolderIcon       = mContext.getDrawable(R.drawable.ic_default_folder);
        clickCallBack     = (AdapterCallbacks)mContext;
        mFileListAdapter  =adapter;
        mFileFiller       =adapter.getmFileFiller();

    }

    public void selectAllFiles() {
        for (int i = 0; i < mFileFiller.getTotalFilesCount(); i++) {
            mDataModel = mFileFiller.getFileAtPosition(i);
            selectFile(i);
        }
        mFileListAdapter.notifyDataSetChanged();
    }


    void selectionOperation(int position){
        mDataModel  = mFileFiller.getFileAtPosition(position);

        if(mDataModel.getSelected()){
            Log.d(TAG, "selectionOperation: fixing a bug in files selection");
            mSelectedFilePaths.remove(mDataModel.getFilePath());
            mDataModel.setSelected(false);
            clickCallBack.decrementSelectionCount();
            if(mDataModel.getFile()){
                mDataModel.setFileIcon(MimeType.getIcon(mDataModel.getFileExtension()));
            }else{
                mDataModel.setFileIcon(mFolderIcon);
            }
        }else{
            selectFile(position);
        }

        mFileListAdapter.notifyItemChanged(position);

    }
    private void selectFile(int position){
        if(!mDataModel.getSelected()) {
            mSelectedPosition.add(position);
            mSelectedFilePaths.add(mDataModel.getFilePath());
            mDataModel.setFileIcon(mSelectedFileIcon);
            mDataModel.setSelected(true);
            clickCallBack.incrementSelectionCount();
        }
    }
    void selectFileInSelectionMode(int position){
        if(mSelectedPosition.size()>0){
            mDataModel=mFileFiller.getFileAtPosition(mSelectedPosition.get(0));
            mDataModel.setSelected(false);
            mDataModel.setFileIcon(MimeType.getIcon(mDataModel.getFileExtension()));
            mFileListAdapter.notifyItemChanged(mSelectedPosition.get(0));
            mSelectedPosition.clear();
            mSelectedFilePaths.clear();
        }
            mDataModel=mFileFiller.getFileAtPosition(position);
            mSelectedPosition.add(position);
            mSelectedFilePaths.add(mDataModel.getFilePath());
            mDataModel.setFileIcon(mSelectedFileIcon);
            mDataModel.setSelected(true);
        mFileListAdapter.notifyItemChanged(position);
    }


    public void setmSelectionMode(Boolean value){
        if(value){
            return;
        }
        //first check if there are select files
        if(mSelectedPosition.size()>0) {
            for (Integer pos : mSelectedPosition) {
                mDataModel =  mFileFiller.getFileAtPosition(pos);
                mDataModel.setSelected(false);
            }
            mSelectedPosition.clear();
            mSelectedFilePaths.clear();
        }
    }

    public void resetFileIcons(){
        for (Integer pos:
                mSelectedPosition) {
            mDataModel = mFileFiller.getFileAtPosition(pos);
            if(mDataModel.getFile()){
                mDataModel.setFileIcon(MimeType.getIcon(mDataModel.getFileExtension()));
            }else{
                mDataModel.setFileIcon(mFolderIcon);
            }
            mFileListAdapter.notifyItemChanged(pos);

        }
    }

    public ArrayList<String> getmSelectedFilePaths() {
        return mSelectedFilePaths;
    }


    void openFile(final String filename) {
        if (SharedData.IS_IN_COPY_MODE) {
            return;
        }
        if (FileUtils.getExtension(filename).equals("pgp")) {
            Log.d(TAG, "openFile: File name is: "+filename);
            if (SharedData.KEY_PASSWORD == null) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.password_dialog_layout);
                dialog.show();
                dialog.findViewById(R.id.cancel_decrypt_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText editText = (EditText) dialog.findViewById(R.id.key_password);
                dialog.findViewById(R.id.decrypt_file_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().length() < 1) {
                            editText.setError("please give me your encryption password");
                            return;
                        } else {
                            SharedData.KEY_PASSWORD = editText.getText().toString();
                            dialog.dismiss();
                            new DecryptTask(
                                    mContext,
                                    mFileListAdapter,
                                    SharedData.DB_PASSWWORD,
                                    SharedData.USERNAME,
                                    FileUtils.CURRENT_PATH+filename,
                                    SharedData.KEY_PASSWORD).execute();
                        }

                    }
                });
            } else {
                new DecryptTask(
                        mContext,
                        mFileListAdapter,
                        SharedData.DB_PASSWWORD,
                        SharedData.USERNAME,
                        FileUtils.CURRENT_PATH+filename,
                        SharedData.KEY_PASSWORD).execute();
            }

        } else {
            //open file
            if(SharedData.EXTERNAL_SDCARD_ROOT_PATH!=null &&
                    FileUtils.CURRENT_PATH.contains(SharedData.EXTERNAL_SDCARD_ROOT_PATH)){
                //open the document file
                //DocumentFile file= FileDocumentUtils.getDocumentFile(FileUtils.getFile())
            }
            String mimeType =
                    MimeTypeMap.getSingleton().
                            getMimeTypeFromExtension(
                                    FileUtils.getExtension(filename
                                    )
                            );

            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = FileProvider.getUriForFile(
                        mContext,
                        mContext.getApplicationContext().getPackageName() + ".provider",
                        FileUtils.getFile(filename)
                );
                intent.setDataAndType(uri, mimeType);
            } else {
                intent.setDataAndType(Uri.fromFile(FileUtils.getFile(filename)), mimeType);
            }
            intent.setAction(Intent.ACTION_VIEW);
            Intent x = Intent.createChooser(intent, "Open with: ");
            mContext.startActivity(x);
        }
    }

    void openFolder(String filename,int position) {
        if(SharedData.STARTED_IN_SELECTION_MODE) {
            mSelectedPosition.clear();
            mSelectedFilePaths.clear();
        }
        if(SharedData.SELECTION_MODE){
            selectionOperation(position);
            return;
        }
        String folderPath = mFileFiller.getCurrentPath() + filename + "/";
        clickCallBack.changeTitle(folderPath);
        mFileFiller.fillData(folderPath, mContext);
        mFileListAdapter.notifyDataSetChanged();
        if (mFileFiller.getTotalFilesCount() < 1) {
            clickCallBack.showNoFilesFragment();
        }
    }
    void startSelectionMode(){
        SharedData.SELECTION_MODE = true;
        clickCallBack.onLongClick();
    }
}
