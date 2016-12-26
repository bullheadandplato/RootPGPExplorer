package com.cryptopaths.cryptofm.tasks;

import java.io.File;
import java.io.IOException;

/**
 * Created by tripleheader on 12/26/16.
 * the file operation tasks needs to performed with the files
 */

public class TasksFileUtils {
    public static File getFile(String path) throws IOException{
        // called to get destination file it must have to be created.
        File temp=new File(path);
        if(!temp.exists()){
            if(temp.createNewFile()){
                return temp;
            }else{
                throw new IOException("cannot create file");
            }
        }else{
            return temp;
        }
    }

}
