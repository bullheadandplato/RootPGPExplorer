package com.cryptopaths.cryptofm.filemanager;

import java.io.File;
import java.util.HashMap;

/**
 * Created by tripleheader on 12/18/16.
 * fill the map of files against path
 */

public class FileFillerWrapper {
    private HashMap<String,DataModelFolders> allFolders=new HashMap<>();
    private HashMap<String,DataModelFiles> allFiles=new HashMap<>();
    private String currentPath;

    public FileFillerWrapper(String currentPath) {
        this.currentPath = currentPath;
        //be sure to change the path in File utilities
        FileUtils.CURRENT_PATH=currentPath;
        File tmp=new File(currentPath);
        for (File f:
             tmp.listFiles()) {
            fillData(f);
        }
    }
    private void fillData(File f){
        if(f.isDirectory()){
            allFolders.put(currentPath,new DataModelFolders(f.getName()));
        }else if(f.isFile()){
            allFiles.put(currentPath,new DataModelFiles(f.getName()));
        }
    }
}
