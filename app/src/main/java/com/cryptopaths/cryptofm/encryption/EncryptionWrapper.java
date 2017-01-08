package com.cryptopaths.cryptofm.encryption;

import java.io.File;
import java.io.InputStream;

/**
 * Created by tripleheader on 1/8/17.
 * choose what encryption method to use.
 * i.e different for different file sizes
 */

public class EncryptionWrapper {
    private static final int TWO_MB=1048576;
    public static void encryptFile(File inputFile,
                                   File outputFile,
                                   File keyFile,
                                   Boolean integrityCheck)
            throws Exception{
        //check file size
        if(inputFile.length()>TWO_MB){
            EncryptionManagement enc=new EncryptionManagement();
            enc.encryptFile(outputFile,inputFile,keyFile,integrityCheck);
        }else{
            EncryptionSmallFileProcessor enc=new EncryptionSmallFileProcessor();
            enc.encryptFile(inputFile,outputFile,keyFile,integrityCheck);
        }

    }
    public static void decryptFile(File inputFile,
                                   File outputFile,
                                   File pubKey,
                                   InputStream secKeyFile,
                                   char[] pass
    )throws Exception{
        if(inputFile.length()>TWO_MB){
            EncryptionManagement dec=new EncryptionManagement();
            dec.decryptFile(inputFile,outputFile,pubKey,secKeyFile,pass);
        }else{
            EncryptionSmallFileProcessor dec=new EncryptionSmallFileProcessor();
            dec.decryptFile(inputFile,outputFile,pubKey,secKeyFile,pass);
        }
    }
}
