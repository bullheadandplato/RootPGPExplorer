package com.osama.cryptofmroot.root;

import java.io.IOException;

/**
 * Created by bullhead on 4/16/17.
 *
 */

public final class RootUtils {
    public static boolean checkRootAccess(){
        try{
            String command="su";
            ProcessBuilder builder=new ProcessBuilder(command);
                Process p=builder.start();
                if(p.exitValue()==0){
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
    }
}
