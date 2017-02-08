package com.cryptopaths.cryptofm.encryption;


import android.util.Log;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPCompressedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;

/**
 * Created by bullhead on 2/8/17.
 */

public class DocumentFileEncryption  {
    private static final String TAG=DocumentFileEncryption.class.getName();
    public boolean encryptFile(InputStream in, OutputStream out, File pubKeyFile,boolean integrityCheck, Date fileDate,String filename){
        boolean status=false;
       try{
           PGPPublicKey encKey=MyPGPUtil.readPublicKey(new FileInputStream(pubKeyFile));
           Security.addProvider(new BouncyCastleProvider());

           PGPEncryptedDataGenerator cPk =
                   new PGPEncryptedDataGenerator(
                           new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).
                                   setWithIntegrityPacket(integrityCheck).
                                   setSecureRandom(
                                           new SecureRandom()
                                   )
                   );

           cPk.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey));

           OutputStream                cOut = cPk.open(out, new byte[1 << 16]);

           PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                   PGPCompressedData.UNCOMPRESSED);

           //PGPUtil.writeFileToLiteralData(comData.open(cOut), PGPLiteralData.BINARY, in, new byte[1 << 16]);
           PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
           OutputStream pOut = lData.open(out, PGPLiteralData.BINARY,filename ,fileDate, new byte[1 << 16]);
           PGPUtil.pipeDocumentFileContents(in,pOut, new byte[1 << 16].length);
           comData.close();

           cOut.close();
           Log.d("encrypt","file successfully encrypted");
           status=true;
       }catch (Exception ex){
           Log.d(TAG, "encryptFile: error in encrypting document file");
           ex.printStackTrace();
           status=false;
       }

        return status;
    }
}
