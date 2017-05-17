package com.osama.cryptofmroot.utils;

/**
 * Created by bullhead on 5/17/17.
 */

public final class SystemUtils {
    public static String getSystemArchitecture(){
        String arch=System.getProperty("os.arch");
        switch (arch){
            case "aarch64": return "i686";
        }
        return arch;
    }
}
