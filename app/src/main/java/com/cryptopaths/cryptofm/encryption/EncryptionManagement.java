package com.cryptopaths.cryptofm.encryption;

import android.accounts.OperationCanceledException;
import android.util.Log;

import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptolib.org.spongycastle.jce.provider.BouncyCastleProvider;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPCompressedData;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPCompressedDataGenerator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPEncryptedData;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPEncryptedDataList;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPException;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPLiteralData;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPOnePassSignatureList;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPrivateKey;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPublicKey;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPublicKeyEncryptedData;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPUtil;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;
/**
 * Created by osama on 10/13/16.
 * the encryption related operations
 */

public class EncryptionManagement implements EncryptionOperation {
    static {
        Security.addProvider(new com.cryptopaths.cryptolib.org.spongycastle.jce.provider.BouncyCastleProvider());
    }
    private KeyManagement keyManagement;
    public EncryptionManagement(){
        keyManagement=new KeyManagement();
    }

    @Override
    public void decryptFile(File inputFile,File outputFile,File pubKey,InputStream secKeyFile,char[] pass)throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
        decryptFile(in, secKeyFile, pass, outputFile.getAbsolutePath(),inputFile.length());
        Log.d("decrypt","look like i can handle it");
        secKeyFile.close();
        in.close();
    }

    @Override
    public  void encryptFile(File outputFile, File inputFile,
                             File pubKeyFile,Boolean integrityCheck
    ) throws Exception {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
        String fileName=inputFile.getPath();
        PGPPublicKey encKey=keyManagement.getPublicKey(pubKeyFile);
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
                PGPCompressedData.ZIP);

        PGPUtil.writeFileToLiteralData(comData.open(cOut), PGPLiteralData.BINARY, new File(fileName), new byte[1 << 16]);

        comData.close();

        cOut.close();
        Log.d("encrypt","file successfully encrypted");
    }
    /**
     * decrypt the passed in message stream
     */
    private void decryptFile(
            InputStream in,
            InputStream keyIn,
            char[]      passwd,
            String      defaultFileName,
            long limit
            )
            throws Exception {
        Log.d("decrypt","yoo nigga decrypting");
        in = PGPUtil.getDecoderStream(in);

        try
        {
            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
            PGPEncryptedDataList enc;

            Object                  o = pgpF.nextObject();
            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList)
            {
                enc = (PGPEncryptedDataList)o;
            }
            else
            {
                enc = (PGPEncryptedDataList)pgpF.nextObject();
            }

            //
            // find the secret key
            //
            Iterator                    it = enc.getEncryptedDataObjects();
            PGPPrivateKey sKey = null;
            PGPPublicKeyEncryptedData pbe = null;
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                    PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

            while (sKey == null && it.hasNext())
            {
                pbe = (PGPPublicKeyEncryptedData)it.next();

                sKey = keyManagement.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
            }

            if (sKey == null)
            {
                Log.d("decrypt", "decryptFile: no key found");
                throw new IllegalArgumentException("password is wrong.");
            }

            InputStream         clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));

            JcaPGPObjectFactory    plainFact = new JcaPGPObjectFactory(clear);

            PGPCompressedData   cData = (PGPCompressedData)plainFact.nextObject();

            InputStream         compressedStream = new BufferedInputStream(cData.getDataStream());
            JcaPGPObjectFactory    pgpFact = new JcaPGPObjectFactory(compressedStream);

            Object              message = pgpFact.nextObject();

            if (message instanceof PGPLiteralData)
            {
                PGPLiteralData ld = (PGPLiteralData)message;
                InputStream unc = ld.getInputStream();
                Log.d("decrypt","now trying with limit: ");
                OutputStream fOut =  new BufferedOutputStream(new FileOutputStream(defaultFileName));
                pipeAll(unc,fOut,limit);


                fOut.close();
            }
            else if (message instanceof PGPOnePassSignatureList)
            {
                throw new PGPException("encrypted message contains a signed message - not literal data.");
            }
            else
            {
                throw new PGPException("message is not a simple encrypted file - type unknown.");
            }

            if (pbe.isIntegrityProtected())
            {
                if (!pbe.verify())
                {
                    System.err.println("message failed integrity check");
                }
                else
                {
                    System.err.println("message integrity check passed");
                }
            }
            else
            {
                System.err.println("no message integrity check");
            }
        }
        catch (PGPException e)
        {
            e.printStackTrace();
            if (e.getUnderlyingException() != null)
            {
                e.getUnderlyingException().printStackTrace();
            }
        }
    }
    private void pipeAll(InputStream inStr,OutputStream outStr,long limit) throws Exception{
        long total = 0;
        byte[] bs = new byte[4096];
        int numRead;
        while ((numRead = inStr.read(bs, 0, bs.length)) >= 0 )
        {
            if(SharedData.IS_TASK_CANCELED){
                throw new OperationCanceledException("operation canceled");
            }
            total += numRead;
            outStr.write(bs, 0, numRead);
            if((limit-total)<numRead){
                return;
            }
        }
    }


}

