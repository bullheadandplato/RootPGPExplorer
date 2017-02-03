package com.cryptopaths.cryptofm.encryption;

import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKey;

import java.io.File;

/**
 * Created by osama on 10/8/16.
 *
 */
public interface KeyOperations  {
    PGPPublicKey getPublicKey(File binaryData)throws Exception;
    PGPKeyRingGenerator generateKey(String email, char[] password) throws Exception;
}
