package com.osama.cryptofmroot.root;

import android.util.Log;

import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.listview.DataModelFiles;
import com.osama.cryptofmroot.filemanager.utils.MimeType;
import com.osama.cryptofmroot.filemanager.utils.SharedData;
import com.osama.cryptofmroot.utils.FileUtils;

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
    private static final int SIZE_INDEX=2;
    private static final int NO_OF_ITEMS_INDEX=1;
    private static final int FILENAME_INDEX=5;


    public static ArrayList<DataModelFiles> getFileNames(String path){
        ArrayList<DataModelFiles> names=new ArrayList<>();
       // Log.d(TAG, "getFileNames: test: "+test.size());
            List<String> test=Shell.SU.run("ls -lAhpog -1 "+path);

        for (int i = 1; i < test.size(); i++) {
            String currentString=test.get(i);
            currentString=currentString.trim().replaceAll(" +"," ");
            //Log.d(TAG, "getFileNames: "+currentString);
            String[] parts=currentString.split(" ");
            //filedates[i]=parts[DATE_INDEX]+" "+parts[DATE_INDEX+1];
            DataModelFiles temp=new DataModelFiles();
            String filename=parts[FILENAME_INDEX];
            for (int j = FILENAME_INDEX; j < parts.length; j++) {
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
                filename=" "+parts[j];
            }
            if(filename.contains("/")){
                temp.setFile(false);
                temp.setFileExtensionOrItems(parts[NO_OF_ITEMS_INDEX]);
                temp.setFileName(filename.substring(0,filename.lastIndexOf('/')));
                temp.setFileIcon(CryptoFM.getContext().getDrawable(R.drawable.ic_default_folder));
            }else{
                temp.setFile(false);
                temp.setFileExtensionOrItems(FileUtils.getExtension(filename));
                temp.setFileName(filename);
                temp.setFileIcon(MimeType.getIcon(FileUtils.getExtension(filename)));
                temp.setEncrypted(FileUtils.isEncryptedFile(filename));
            }
            temp.setFileDate(parts[DATE_INDEX]+" "+parts[DATE_INDEX+1]);
            names.add(temp);
        }
        return names;
    }
}
