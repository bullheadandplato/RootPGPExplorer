package com.cryptopaths.cryptofm.encryption;

import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPSecretKey;

import java.io.File;
import java.io.InputStream;

/**
 * Created by osama on 10/8/16.
 */
public interface KeyOperations  {
    public PGPPublicKey getPublicKey(File binaryData)throws Exception;
    public PGPSecretKey getSecretKey(InputStream binaryData, long keyID) throws Exception;
    public PGPKeyRingGenerator generateKey(String email, char[] password) throws Exception;


}
