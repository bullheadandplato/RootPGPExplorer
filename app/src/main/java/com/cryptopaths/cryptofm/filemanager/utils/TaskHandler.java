package com.cryptopaths.cryptofm.filemanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.listview.FileSelectionManagement;
import com.cryptopaths.cryptofm.startup.OptionActivity;
import com.cryptopaths.cryptofm.tasks.DecryptTask;
import com.cryptopaths.cryptofm.tasks.DeleteTask;
import com.cryptopaths.cryptofm.tasks.EncryptTask;
import com.cryptopaths.cryptofm.tasks.MoveTask;
import com.cryptopaths.cryptofm.tasks.RenameTask;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/13/17.
 * task executor
 */

public class TaskHandler {
    private EncryptTask             mEncryptTask;
    private DecryptTask             mDecryptTask;
    private FileListAdapter         mAdapter;
    private Context                 mContext;
    private FileSelectionManagement mFileSelectionManagement;
    private ArrayList<String>       mSelectedFiles;
    public TaskHandler(Context context,FileListAdapter adapter,FileSelectionManagement m){
        this.mContext=context;
        mAdapter=adapter;
        mFileSelectionManagement=m;
    }

    public void renameFile(){
        final Dialog dialog = UiUtils.createDialog(
                mContext,
                "Rename file",
                "rename"
        );

        final EditText folderEditText = (EditText)dialog.findViewById(R.id.foldername_edittext);
        Button okayButton			  = (Button)dialog.findViewById(R.id.create_file_button);
        String currentFileName		  = mFileSelectionManagement.getmSelectedFilePaths().get(0);

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
                            mFileSelectionManagement.getmSelectedFilePaths().get(0),
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
                                (ArrayList<String>) mFileSelectionManagement.getmSelectedFilePaths().clone());
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

    public void decryptFile(final String username, final String keypass, final String dbpass,ArrayList<String> files) {

        SharedData.IS_TASK_CANCELED=false;
        ArrayList<String> tmp=new ArrayList<>();
        int size=files.size();
        for (int i = 0; i < size; i++) {
            if(!SharedData.checkIfInRunningTask(files.get(i))){
                tmp.add(files.get(i));
            }
        }
        if(tmp.size()<1){
            Toast.makeText(
                    mContext,
                    "Another operation is already running on files, please wait",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        SharedData.CURRENT_RUNNING_OPERATIONS=tmp;

        mDecryptTask=new DecryptTask(
                mContext,
                mAdapter,
                files,
                dbpass,
                username,
                keypass
        );
        try{
            mDecryptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }catch (IllegalStateException ex){
            Toast.makeText(
                    mContext,
                    "Already decrypting files",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    public void encryptTask(ArrayList<String> files){
        //check if user hasn't generate keys
        SharedPreferences prefs=mContext.getSharedPreferences("done",Context.MODE_PRIVATE);
        if(!prefs.getBoolean("keys_gen",false)){
            //generate keys first
            generateKeys();
        }
        SharedData.IS_TASK_CANCELED=false;
        ArrayList<String> tmp=new ArrayList<>();
        int size=files.size();
        for (int i = 0; i < size; i++) {
            if(!SharedData.checkIfInRunningTask(files.get(i))){
                tmp.add(files.get(i));
            }
        }
        if(tmp.size()<1){
            Toast.makeText(
                    mContext,
                    "Another operation is already running on files, please wait",
                    Toast.LENGTH_LONG
            ).show();
            return ;
        }
        SharedData.CURRENT_RUNNING_OPERATIONS=tmp;
        mEncryptTask=new EncryptTask(
                mContext,
                mAdapter,
                files
        );
        mEncryptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void generateKeys() {
        //get the password
        Intent intent = new Intent(mContext, OptionActivity.class);
        mContext.startActivity(intent);
    }

    public DecryptTask getDecryptTask() {
        return mDecryptTask;
    }

    public EncryptTask getEncryptTask() {
        return mEncryptTask;
    }

    public void moveFiles(String dest,FileListAdapter m){
        //make sure files have been placed
        assert mSelectedFiles!=null;
        if(mSelectedFiles.size()<1){
            Log.d("MoveTask", "moveFiles: files are not added");
        }
        new MoveTask(mContext,mSelectedFiles,dest,m).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setmSelectedFiles(ArrayList<String> mSelectedFiles) {
        this.mSelectedFiles = mSelectedFiles;
    }
}
