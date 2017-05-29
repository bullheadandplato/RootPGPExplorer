package com.slownet5.pgprootexplorer.filemanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.listview.FileListAdapter;
import com.slownet5.pgprootexplorer.filemanager.listview.FileSelectionManagement;
import com.slownet5.pgprootexplorer.startup.OptionActivity;
import com.slownet5.pgprootexplorer.tasks.DecryptTask;
import com.slownet5.pgprootexplorer.tasks.EncryptTask;

import java.util.ArrayList;

/**
 * Created by bullhead on 5/29/17.
 *
 */

public class TaskHandlerWrapper extends TaskHandler{
    private Context mContext;
    private ArrayList<String> mSelectedFiles;

    public TaskHandlerWrapper(Context context, FileListAdapter adapter){
        super(context,adapter);
        this.mContext=context;
    }

    @Override
    public DecryptTask getDecryptTask() {
        return super.getDecryptTask();
    }

    @Override
    public EncryptTask getEncryptTask() {
        return super.getEncryptTask();
    }

    @Override
    public void encryptTask(final ArrayList<String> tmp) {

        ArrayList<String> files=new ArrayList<>(tmp);
         //check if user hasn't generate keys
        if(!SharedData.KEYS_GENERATED){
            //generate keys first
            generateKeys();
            return;
        }

        if(!isOperationNotRunning(files)){
            return;
        }

        if(SharedData.ASK_ENCRYPTION_CONFIG){
           showAskDialog(files);
        }else{
         super.encryptTask(files);
        }

    }

    @Override
    public void decryptFile(final String username, final String keypass,final String dbpass, ArrayList<String> tmp) {
        final ArrayList<String> files=new ArrayList<>(tmp);
        if (!SharedData.KEYS_GENERATED) {
            //generate keys first
            generateKeys();
            return;
        }
        if ((SharedData.KEY_PASSWORD == null || !SharedData.ASK_KEY_PASSS_CONFIG)) {
            showPasswordDialog(username,dbpass,files);
        } else {
            if (!isOperationNotRunning(files)) {
                return;
            }
            super.decryptFile(username,keypass,dbpass,files);
        }
    }

    public void moveFiles(String dest, FileListAdapter m) {
        //make sure files have been placed
        if(mSelectedFiles.size()<1){
            Log.d("MoveTask", "moveFiles: files are not added");
        }
        if(!isOperationNotRunning(mSelectedFiles)){
            return;
        }
        super.moveFiles(dest,m,mSelectedFiles);
    }

    @Override
    public void compressTask(ArrayList<String> tmp, boolean uncompress) {
        ArrayList<String> files=new ArrayList<>(tmp);
        if(!isOperationNotRunning(files)){
            return;
        }
        super.compressTask(files, uncompress);
    }

    @Override
    public void deleteFile(final ArrayList<String> tmp) {
        final ArrayList<String> files=new ArrayList<>(tmp);
        if(!isOperationNotRunning(files)){
            return;
        }
        final AlertDialog dialog=new AlertDialog.Builder(mContext).create();
        dialog.setTitle("Delete confirmation");
        dialog.setMessage("Do you really want to delete these files(s)?");
        dialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                          dialog.dismiss();
                        TaskHandlerWrapper.super.deleteFile(files);
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


    public void renameFile(final ArrayList<String> tmp) {
        final ArrayList<String> files=new ArrayList<>(tmp);

        if(!isOperationNotRunning(files)){
            return;
        }
        final Dialog dialog = UiUtils.createDialog(
                mContext,
                "Rename file",
                "rename"
        );

        final EditText folderEditText = (EditText)dialog.findViewById(R.id.foldername_edittext);
        Button okayButton			  = (Button)dialog.findViewById(R.id.create_file_button);
        String currentFileName		  = files.get(0);

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
                    TaskHandlerWrapper.super.renameFile(files.get(0), folderName);
                   dialog.dismiss();
                }
            }
        });
    }

    private void showAskDialog(final ArrayList<String> files){
         AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
            builder.setTitle("Encryption");
            builder.setMessage("Do you really want to encrypt the selected files?");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TaskHandlerWrapper.super.encryptTask(files);
                }
            });
            builder.show();
    }
    private void showPasswordDialog(final String username,final String dbpass,final ArrayList<String> files){
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
                        TaskHandlerWrapper.super.decryptFile(username,SharedData.KEY_PASSWORD,dbpass,files);
                        dialog.dismiss();
                    }
                }
            });

    }

     private boolean isOperationNotRunning(ArrayList<String> files){
       if(SharedData.checkIfInRunningTask(files)){
            Toast.makeText(mContext,"Another operation is already running on selected files. please wait",
                    Toast.LENGTH_LONG).show();
            return false;
       }
       return true;
    }
     private void generateKeys() {
        //show user of what Im going to do
        final AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
        dialog.setCancelable(false);
        dialog.setTitle("Keys not generated");
        dialog.setMessage("Looks like you haven't generated your keys. You need to generate keys now");
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 //get the password
                Intent intent = new Intent(mContext, OptionActivity.class);
                mContext.startActivity(intent);
            }
        });
        dialog.show();
    }

    public void setmSelectedFiles(ArrayList<String> mSelectedFiles) {
        this.mSelectedFiles = new ArrayList<>(mSelectedFiles);
    }

    public ArrayList<String> getmSelectedFiles() {
        return mSelectedFiles;
    }
}
