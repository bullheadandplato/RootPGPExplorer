/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.encryption;


import android.util.Log;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPCompressedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyEncryptedData;
import org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by bullhead on 2/8/17.
 *
 */

public class DocumentFileEncryption  {
    private static final String TAG=DocumentFileEncryption.class.getName();
    public static boolean encryptFile(InputStream in, OutputStream out, File pubKeyFile,
                               boolean integrityCheck, Date fileDate,String filename){
        boolean status;
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

           PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
           OutputStream pOut = lData.open(comData.open(cOut), PGPLiteralData.BINARY,filename ,fileDate, new byte[1 << 16]);
           PGPUtil.pipeDocumentFileContents(in,pOut, new byte[1 << 16].length);
           comData.close();

           cOut.close();
           Log.d(TAG,"file successfully encrypted");
           status=true;
       }catch (Exception ex){
           Log.d(TAG, "encryptFile: error in encrypting document file");
           ex.printStackTrace();
           status=false;
       }

        return status;
    }
    public static boolean decryptFile(
            InputStream in,
            InputStream keyIn,
            char[]      passwd,
            OutputStream outFile
    ){

        Log.d(TAG,"decrypting document file");

        try
        {
            in = PGPUtil.getDecoderStream(in);

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
            PGPPrivateKey sKey = null;
            PGPPublicKeyEncryptedData pbe = null;
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                    PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

            while (sKey == null && it.hasNext())
            {
                pbe = (PGPPublicKeyEncryptedData)it.next();

                sKey = KeyManagement.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
            }

            if (sKey == null)
            {
                Log.d(TAG, "decryptFile: no key found");
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
                PGPLiteralData ld   = (PGPLiteralData)message;
                InputStream unc     = ld.getInputStream();
                //OutputStream fOut =  new BufferedOutputStream(new FileOutputStream(defaultFileName));
                Streams.pipeAll(unc,outFile);

                outFile.close();
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
            return false;
        }catch (IOException ex){
            Log.d(TAG, "decryptFile: Error decrypting file");
            ex.printStackTrace();
            return false;
        }catch (NoSuchProviderException ex){
            Log.d(TAG, "decryptFile: No such provider exception");
            return false;
        }
        return true;
    }
}
