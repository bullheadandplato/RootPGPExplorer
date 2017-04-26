package com.osama.cryptofmroot.root;

import android.util.Log;

import com.osama.RootTools.RootTools;
import com.osama.cryptofmroot.filemanager.listview.DataModelFiles;
import com.osama.cryptofmroot.filemanager.listview.FileFillerWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by bullhead on 4/16/17.
 *
 */

public final class RootUtils {
    private static final String TAG=RootUtils.class.getCanonicalName();

    public static ArrayList<DataModelFiles> getFileNames(String path){
        ArrayList<DataModelFiles> names=new ArrayList<>();
        try {
            Process process=Runtime.getRuntime().exec("su -c ls -A -1 "+path);
            process.waitFor();
            InputStream stream=process.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line=bufferedReader.readLine())!=null){
                File file=new File(path,line);
                names.add(new DataModelFiles(file));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return names;
    }
}
