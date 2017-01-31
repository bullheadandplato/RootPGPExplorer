package com.cryptopaths.cryptofm.encryption;

import com.cryptopaths.cryptolib.org.spongycastle.jce.provider.BouncyCastleProvider;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPCompressedData;
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
import com.cryptopaths.cryptolib.org.spongycastle.util.io.Streams;

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
 * Created by tripleheader on 1/8/17.
 * encryption operation for the file size less than 2Mb
 */

public class EncryptionSmallFileProcessor implements EncryptionOperation{
    static {
        Security.insertProviderAt(new com.cryptopaths.cryptolib.org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    @Override
    public void encryptFile(File inputFile, File outputFile, File keyFile, Boolean integrityCheck) throws Exception {
        try
        {
            byte[] bytes = MyPGPUtil.compressFile(inputFile.getAbsolutePath(),PGPCompressedData.ZIP);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
            Security.addProvider(new BouncyCastleProvider());
            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(integrityCheck).setSecureRandom(new SecureRandom()));
            PGPPublicKey key= MyPGPUtil.readPublicKey(new FileInputStream(keyFile));
            encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(key).setProvider("BC"));

            OutputStream cOut = encGen.open(out, bytes.length);

            cOut.write(bytes);
            cOut.close();

            out.close();

        }
        catch (PGPException e)
        {
            System.err.println(e);
            if (e.getUnderlyingException() != null)
            {
                e.getUnderlyingException().printStackTrace();
            }
        }

    }

    @Override
    public void decryptFile(File inputFile, File outputFile, File pubKeyFile, InputStream secKeyFile, char[] pass) throws Exception {
        InputStream in =new BufferedInputStream(new FileInputStream(inputFile));
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
            Iterator it = enc.getEncryptedDataObjects();
            PGPPrivateKey sKey                  = null;
            PGPPublicKeyEncryptedData pbe       = null;
            PGPSecretKeyRingCollection pgpSec   = new PGPSecretKeyRingCollection(
                    PGPUtil.getDecoderStream(secKeyFile), new JcaKeyFingerprintCalculator());

            while (sKey == null && it.hasNext())
            {
                pbe = (PGPPublicKeyEncryptedData)it.next();

                sKey = MyPGPUtil.findSecretKey(pgpSec, pbe.getKeyID(), pass);
            }

            if (sKey == null)
            {
                throw new IllegalArgumentException("secret key for message not found.");
            }

            InputStream         clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));

            JcaPGPObjectFactory    plainFact = new JcaPGPObjectFactory(clear);

            Object              message = plainFact.nextObject();

            if (message instanceof PGPCompressedData)
            {
                PGPCompressedData   cData       = (PGPCompressedData)message;
                JcaPGPObjectFactory    pgpFact  = new JcaPGPObjectFactory(cData.getDataStream());

                message = pgpFact.nextObject();
            }

            if (message instanceof PGPLiteralData)
            {
                PGPLiteralData ld = (PGPLiteralData)message;


                InputStream unc = ld.getInputStream();
                OutputStream fOut = new BufferedOutputStream(new FileOutputStream(outputFile));

                Streams.pipeAll(unc, fOut);

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
        in.close();
        secKeyFile.close();
    }
}
