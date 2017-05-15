package com.osama.cryptofmroot.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.osama.cryptofmroot.filemanager.listview.FileListAdapter;
import com.osama.cryptofmroot.filemanager.utils.SharedData;
import com.osama.cryptofmroot.filemanager.utils.UiUtils;
import com.osama.cryptofmroot.root.RootUtils;
import com.osama.cryptofmroot.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by bullhead on 5/15/17.
 *
 */

public class CompressTask extends AsyncTask<Void,String,Boolean>{
    private MyProgressDialog mProgressDialog;
    private ArrayList<String> mFiles;
    private String errorMessage;
    private FileListAdapter mAdapter;
    private boolean uncompress=false;
    private String mCurrentPath;
    private boolean isRootPath=false;
    private File rootHandlingFile;
    private Context mContex;
    private String zipFileName;
    private String rootHandlingPath= SharedData.FILES_ROOT_DIRECTORY+"CryptoFM/comp";

    public CompressTask(ArrayList<String> mFiles, Context mContext,  FileListAdapter mAdapter, boolean uncompress,String mCurrentPath) {
        this.mFiles = mFiles;
        this.mAdapter = mAdapter;
        this.uncompress=uncompress;
        this.mCurrentPath=mCurrentPath;
        this.mContex=mContext;
        String title="Compressing files";
        if(uncompress){
            title="Uncompressing files";
        }
        this.mProgressDialog=new MyProgressDialog(mContext,title,this);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if(RootUtils.isRootPath(mFiles.get(0))){
                isRootPath=true;
                rootHandlingPath=rootHandlingPath+mCurrentPath;
                rootHandlingFile=new File(rootHandlingPath);
                rootHandlingFile.mkdirs();
                ArrayList<String> tmp=new ArrayList<>();
                for (String path:mFiles) {
                    String filename=FileUtils.getFile(path).getName();
                    RootUtils.copyFile(path,rootHandlingPath+filename);
                    tmp.add(rootHandlingPath+filename);
                }
                mFiles=tmp;
            }
            if(uncompress){
                uncompress(FileUtils.getFile(mFiles.get(0)));
                if(isRootPath){
                    File f=FileUtils.getFile(mFiles.get(0));
                    f.delete();
                    FileUtils.notifyChange(mContex,f.getAbsolutePath());
                }
            }else{
                List<File> tmp=new ArrayList<>();
                for (String p:mFiles) {
                    tmp.add(FileUtils.getFile(p));
                }
                if(isRootPath){
                    compressFile(FileUtils.getFile(rootHandlingPath),tmp);
                    for (File f:tmp) {
                        f.delete();
                    }
                    RootUtils.copyFile(rootHandlingPath+zipFileName,mCurrentPath);
                    FileUtils.getFile(rootHandlingPath+zipFileName).delete();
                }else{
                    compressFile(FileUtils.getFile(mCurrentPath),tmp);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            errorMessage=e.getMessage();
            return false;
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Compressing files");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        mProgressDialog.dismiss(errorMessage);
        UiUtils.reloadData(mAdapter);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        try{
            int progress=Integer.valueOf(values[0]);
            mProgressDialog.setProgress(progress);
        }catch (NumberFormatException e){
            mProgressDialog.setMessage(values[0]);
        }
    }

    private boolean compressFile(File parent, List<File> files) throws Exception{
        boolean success = false;
        zipFileName=files.get(0).getName()+".zip";
            File dest = new File(parent, zipFileName);
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(dest));
            compressFile("", zout, files.toArray(new File[files.size()]));
            zout.close();
        if(!isRootPath){
            FileUtils.notifyChange(mContex,dest.getAbsolutePath());
        }
            success = true;
        return success;
    }

    private void compressFile(String currentDir, ZipOutputStream zout, File[] files) throws Exception {
        byte[] buffer = new byte[1024];
        for (File fi : files) {
            if (fi.isDirectory()) {
                compressFile(currentDir + "/" + fi.getName(), zout, fi.listFiles());
                continue;
            }
            publishProgress(fi.getName());
            publishProgress("+0");
            ZipEntry ze = new ZipEntry(currentDir + "/" + fi.getName());
            FileInputStream fin = new FileInputStream(fi.getPath());
            zout.putNextEntry(ze);
            int length;
            long readData=0;
            final long totalFileLength=fi.length();
            while ((length = fin.read(buffer)) > 0) {
                zout.write(buffer, 0, length);
                 readData+=length;
                doProgress(totalFileLength,readData);
            }
            zout.closeEntry();
            fin.close();
        }
    }

    private boolean uncompress(File zipFile) {
        boolean success = false;
        try {
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            File destFolder = new File(zipFile.getParent(), FileUtils.getNameFromFilename(zipFile.getName()));
            destFolder.mkdirs();
            while ((entry = zis.getNextEntry()) != null) {
                publishProgress(entry.getName().substring(1));
                publishProgress(""+0);
                File dest = new File(destFolder, entry.getName());
                dest.getParentFile().mkdirs();

                if(entry.isDirectory()) {
                    if (!dest.exists()) {
                        dest.mkdirs();
                    }
                } else {
                    int size;
                    byte[] buffer = new byte[2048];
                    FileOutputStream fos = new FileOutputStream(dest);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
                    long readData=0;
                    final long totalFile=entry.getSize();
                    while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, size);
                        readData+=size;
                        doProgress(readData,totalFile);
                    }
                    bos.flush();
                    bos.close();
                    FileUtils.notifyChange(mContex,dest.getAbsolutePath());
                }
                zis.closeEntry();
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
    private void doProgress(final long totalFileLength,long readData){
        double progress=(double) readData/(double) totalFileLength;
            if(mProgressDialog.isInNotifyMode()){
                mProgressDialog.setProgress((int)(progress*100));
            }else {
                publishProgress(""+(int)(progress*100));
            }
    }
}
