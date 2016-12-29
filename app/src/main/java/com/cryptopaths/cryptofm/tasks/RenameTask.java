package com.cryptopaths.cryptofm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cryptopaths.cryptofm.filemanager.FileBrowserActivity;
import com.cryptopaths.cryptofm.filemanager.FileFillerWrapper;
import com.cryptopaths.cryptofm.filemanager.FileListAdapter;

import java.io.File;

/**
 * Created by home on 12/28/16.
 * file rename task
 */

public class RenameTask extends AsyncTask<Void,Void,String> {
    private Context         mContext;
    private String          mFilePath;
    private ProgressDialog  mProgressDialog;
    private String          mRenamed;
    private FileListAdapter mAdapter;

    public RenameTask(Context con,FileListAdapter adapter,String filePath,String rename){
        this.mContext   = con;
        this.mFilePath  = filePath;
        this.mRenamed   = rename;
        this.mAdapter   = adapter;
        mProgressDialog = new ProgressDialog(mContext);
    }
    @Override
    protected String doInBackground(Void... voids) {
        try{
            File file = TasksFileUtils.getFile(mFilePath);
            String filename=file.getPath();
            filename=filename.replace(file.getName(),"");
            File renamedFile = new File(filename+mRenamed);
            if(file.renameTo(renamedFile)){
                return "successfully renamed.";
            }
        }catch (Exception ex){
            ex.getMessage();
            return "cannot rename file.";
        }
        return "cannot rename file.";
    }

    @Override
    protected void onPostExecute(String s) {

        mProgressDialog.dismiss();
        Toast.makeText(
                mContext,
                s,
                Toast.LENGTH_LONG
        ).show();

    }
}
