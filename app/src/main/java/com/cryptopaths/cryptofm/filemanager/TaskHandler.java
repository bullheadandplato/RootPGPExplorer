package com.cryptopaths.cryptofm.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.tasks.DecryptTask;
import com.cryptopaths.cryptofm.tasks.DeleteTask;
import com.cryptopaths.cryptofm.tasks.EncryptTask;
import com.cryptopaths.cryptofm.tasks.RenameTask;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/13/17.
 * task executor
 */

class TaskHandler {
    private EncryptTask     mEncryptTask;
    private DecryptTask     mDecryptTask;
    private FileListAdapter mAdapter;
    private Context         mContext;

    TaskHandler(Context context){
        this.mContext=context;
        mAdapter=SharedData.getInstance().getFileListAdapter();
    }

    public void renameFile(){
        final Dialog dialog = UiUtils.createDialog(
                mContext,
                "Rename file",
                "rename"
        );

        final EditText folderEditText = (EditText)dialog.findViewById(R.id.foldername_edittext);
        Button okayButton			  = (Button)dialog.findViewById(R.id.create_file_button);
        String currentFileName		  = SharedData.getInstance().getFileListAdapter().getmSelectedFilePaths().get(0);

        currentFileName = currentFileName.substring(
                currentFileName.lastIndexOf('/')+1,
                currentFileName.length()
        );
        folderEditText.setText(currentFileName);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName=folderEditText.getText().toString();
                if(folderName.length()<1){
                    folderEditText.setError("Give me the folder name");
                }else{
                    new RenameTask(
                            mContext,
                            mAdapter,
                            mAdapter.getmSelectedFilePaths().get(0),
                            folderName
                    ).execute();
                    dialog.dismiss();
                }
            }
        });
    }
    public void deleteFile(){
        AlertDialog dialog=new AlertDialog.Builder(mContext).create();
        dialog.setTitle("Delete confirmation");
        dialog.setMessage("Do you really want to delete these files(s)?");
        dialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteTask task=new DeleteTask(
                                mContext,
                                mAdapter,
                                (ArrayList<String>) mAdapter.getmSelectedFilePaths().clone());
                        task.execute();

                    }
                });
        dialog.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing
                    }
                });
        dialog.show();
    }

    public void decryptFile(final String username, final String keypass, final String dbpass) {
        Log.d("decrypt", "onActionItemClicked: no mkxsdcfvgbyhnjmey pass is not null");
        mDecryptTask=new DecryptTask(
                mContext,
                mAdapter,
                (ArrayList<String>) mAdapter.getmSelectedFilePaths().clone(),
                dbpass,
                username,
                keypass
        );
        mDecryptTask.execute();
    }
    public EncryptTask encryptTask(ArrayList<String> files){
        mEncryptTask=new EncryptTask(
                mContext,
                mAdapter,
                files
        );
        mEncryptTask.execute();
        return mEncryptTask;
    }

    public DecryptTask getDecryptTask() {
        return mDecryptTask;
    }

    public EncryptTask getEncryptTask() {
        return mEncryptTask;
    }

    public void moveFiles(){


    }
}
