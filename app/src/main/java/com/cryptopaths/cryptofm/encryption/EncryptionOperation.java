package com.cryptopaths.cryptofm.encryption;

import java.io.File;
import java.io.InputStream;

/**
 * Created by osama on 10/13/16.
 */

public interface EncryptionOperation {
    boolean encryptFile(File inputFile, File outputFile, File keyFile, Boolean integrityCheck)throws Exception ;
    boolean decryptFile(File inputFile, File outputFile, File pubKeyFile, InputStream secKeyFile, char[] pass)throws Exception;
}
