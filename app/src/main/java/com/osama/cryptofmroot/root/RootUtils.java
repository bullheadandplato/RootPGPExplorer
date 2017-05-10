package com.osama.cryptofmroot.root;

import android.util.Log;

import com.osama.RootTools.RootTools;
import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.listview.DataModelFiles;
import com.osama.cryptofmroot.filemanager.utils.MimeType;
import com.osama.cryptofmroot.filemanager.utils.SharedData;
import com.osama.cryptofmroot.utils.FileUtils;

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


    public static ArrayList<DataModelFiles> getFileNames(String path){
        ArrayList<DataModelFiles> names=new ArrayList<>();
        Log.d(TAG, "getFileNames: path is: "+path);
       // Log.d(TAG, "getFileNames: test: "+test.size());
            List<String> test=Shell.SU.run("ls -lAhpog -1 "+path);

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
                temp.setFileIcon(CryptoFM.getContext().getDrawable(R.drawable.ic_default_folder));
            }else{
                temp.setFile(true);
                temp.setFileExtensionOrItems(FileUtils.getExtension(filename));
                temp.setFileName(filename);
                temp.setFileIcon(MimeType.getIcon(FileUtils.getExtension(filename)));
                temp.setEncrypted(FileUtils.isEncryptedFile(filename));
            }
            temp.setFileDate(parts[DATE_INDEX]+" "+parts[DATE_INDEX+1]);
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
         Runtime.getRuntime().exec("su -c cp '" +source + "' '"+destination+"'");
    }
    public static void deleteFile(String filename){
        Shell.SU.run("rm -rf '"+filename+"'");
    }

    public static void createFolder(String s) {
        Shell.SU.run("mkdir '"+s+"'");
    }
    public static void renameFile(String from,String to){
        Shell.SU.run("mv '"+from+ "' '"+to+"'");
    }

    public static void chmod666(String filename) {
        Log.d(TAG, "chmod666: changing file permision.");
        Shell.SU.run("chmod 666 \""+filename+"\"");
    }
}
