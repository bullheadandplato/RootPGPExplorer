package com.osama.cryptofm.encryption;

import android.util.Log;

import java.io.File;
import java.io.InputStream;

/**
 * Created by tripleheader on 1/8/17.
 * choose what encryption method to use.
 * i.e different for different file sizes
 */

public class EncryptionWrapper {
    private static final int TWO_MB=2097152;
    public static boolean encryptFile(File inputFile,
                                   File outputFile,
                                   File keyFile,
                                   Boolean integrityCheck)
            throws Exception{
        //check file size
        Log.d("wrapper", "encryptFile: size/length is: "+inputFile.length());
        if(inputFile.length()>TWO_MB){
            EncryptionManagement enc=new EncryptionManagement();
            return enc.encryptFile(outputFile,inputFile,keyFile,integrityCheck);
        }else{
            EncryptionSmallFileProcessor enc=new EncryptionSmallFileProcessor();
            return enc.encryptFile(inputFile,outputFile,keyFile,integrityCheck);
        }

    }
    public static boolean decryptFile(File inputFile,
                                   File outputFile,
                                   File pubKey,
                                   InputStream secKeyFile,
                                   char[] pass
    )throws Exception{
        Log.d("wrapper", "decryptFile: size/length is: "+inputFile.length());
        if(inputFile.length()>TWO_MB){
            EncryptionManagement dec=new EncryptionManagement();
            return dec.decryptFile(inputFile,outputFile,pubKey,secKeyFile,pass);
        }else{
            EncryptionSmallFileProcessor dec=new EncryptionSmallFileProcessor();
            return dec.decryptFile(inputFile,outputFile,pubKey,secKeyFile,pass);
        }
    }
}
