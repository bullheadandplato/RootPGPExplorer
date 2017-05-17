/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.root;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.slownet5.pgprootexplorer.CryptoFM;
import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.listview.DataModelFiles;
import com.slownet5.pgprootexplorer.filemanager.utils.MimeType;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by bullhead on 4/16/17.
 *
 */

public final class RootUtils {
    private static final String TAG=RootUtils.class.getCanonicalName();
    private static final int DATE_INDEX=3;
    private static final int NO_OF_ITEMS_INDEX=1;
    private static final int FILENAME_INDEX=5;
    private static final String TOYBOX_PATH="/cryptofm/toybox ";


    public static ArrayList<DataModelFiles> getFileNames(String path){
        ArrayList<DataModelFiles> names=new ArrayList<>();
        Log.d(TAG, "getFileNames: path is: "+path);
       // Log.d(TAG, "getFileNames: test: "+test.size());
            List<String> test=Shell.SU.run(TOYBOX_PATH+"ls -lAhpog -1 "+path);

        for (int i = 1; i < test.size(); i++) {
            String currentString=test.get(i);
            currentString=currentString.trim().replaceAll(" +"," ");
            String[] parts=currentString.split(" ");
            DataModelFiles temp=new DataModelFiles();
            String filename=parts[FILENAME_INDEX];
            for (int j = FILENAME_INDEX+1; j < parts.length; j++) {
                if(parts[j].contains("->")){
                    String linksPath="";
                    for (int k = j+1; k <parts.length ; k++) {
                        linksPath=linksPath+parts[k];
                    }
                    Log.d(TAG, "getFileNames: Link path is: "+linksPath);
                    SharedData.symbolicLinks.put(filename,linksPath);
                    filename=filename+"/";
                    break;
                }
                filename=filename+" "+parts[j];
            }
            filename=filename.trim();
            if(filename.contains("/")){
                temp.setFile(false);
                temp.setFileExtensionOrItems(parts[NO_OF_ITEMS_INDEX]+" links");
                temp.setFileName(filename.substring(0,filename.lastIndexOf('/')));
                temp.setFileIcon(ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_default_folder));
            }else{
                temp.setFile(true);
                temp.setFileExtensionOrItems(FileUtils.getExtension(filename));
                temp.setFileName(filename);
                temp.setFileIcon(MimeType.getIcon(FileUtils.getExtension(filename)));
                temp.setEncrypted(FileUtils.isEncryptedFile(filename));
            }
            temp.setFileDate(parts[DATE_INDEX]);
            temp.setmFilePath(path+filename);
            names.add(temp);
        }
        return names;
    }

    public static boolean isRootPath(String filename) {
        return !filename.contains(SharedData.FILES_ROOT_DIRECTORY);
    }
    public static void mountRw(){
        Shell.SU.run("mount -o rw,remount /");
    }
    public static void copyFile(String source,String destination) throws IOException {
        mountRw();
        String command="cp -r \""+source+"\" \""+destination+"\"";
        Log.d(TAG, "copyFile: Command is: "+command);
         //Runtime.getRuntime().exec("su -c cp -r \"" +source + "\" \""+destination+"\"");
        Shell.SU.run(command);
    }
    public static void deleteFile(String filename){
        mountRw();
        Shell.SU.run("rm -rf \""+filename+"\"");
    }

    public static void createFolder(String s) {
        mountRw();
        Shell.SU.run("mkdir '"+s+"'");
    }
    public static void renameFile(String from,String to){
        Log.d(TAG, "renameFile: "+from+" to "+to);
        mountRw();
        Shell.SU.run("mv '"+from+ "' '"+to+"'");
    }

    public static void chmod666(String filename) {
        Log.d(TAG, "chmod666: changing file permision.");
        Shell.SU.run("chmod 666 \""+filename+"\"");
        
        //test

        File file=FileUtils.getFile(filename);
        if(file.canRead()){
            Log.d(TAG, "chmod666: Can read files");
        }else{
            Log.d(TAG, "chmod666: Cannot read file");
        }
    }
    public static void voidSElinuxApp(){
        String ds="supolicy --live \"allow untrusted_app rootfs file { append create open execute write relabelfrom link unlink ioctl getattr setattr read rename lock mounton } \"";
      String ds1 ="supolicy --live \"allow untrusted_app rootfs dir { append create open execute write relabelfrom link unlink ioctl getattr setattr read rename lock mounton } \"";
       Shell.SU.run(ds);
        Shell.SU.run(ds1);
    }
    public static void  restoreSElinuxApp(){
        String ds="supolicy --live \"deny untrusted_app rootfs file { open rmdir append create execute write relabelfrom link unlink ioctl getattr setattr read rename lock mounton }\"";

        String ds1 ="supolicy --live \"deny untrusted_app rootfs dir { append create open execute write relabelfrom link unlink ioctl getattr setattr read rename lock mounton } \"";
       Shell.SU.run(ds);
        Shell.SU.run(ds1);
    }
    public static void restoreCon(String filename){
        Shell.SU.run("restorecon \""+filename+"\"");
    }

    public static void createNewFile(String absolutePath) {
        mountRw();
        Shell.SU.run("touch \""+absolutePath+"\"");
    }

    public static void initRoot(String toyboxPath){
        mountRw();
        Shell.SU.run("mkdir /cryptofm");
        Shell.SU.run("mv "+ toyboxPath+ " /cryptofm");
        Shell.SU.run("chmod 667 /cryptofm/toybox");
    }

    public static boolean toyboxExist() {
        List<String> tmp=Shell.SU.run("ls /cryptofm/");

        return tmp.size()>0 && tmp.get(0).equals("toybox");

    }
}
