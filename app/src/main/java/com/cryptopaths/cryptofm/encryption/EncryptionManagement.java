package com.cryptopaths.cryptofm.encryption;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import org.spongycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPCompressedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPObjectFactory;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPBEEncryptedData;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyEncryptedData;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.jcajce.JcaPGPSecretKeyRingCollection;
import org.spongycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.util.io.Streams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by osama on 10/13/16.
 */

public class EncryptionManagement implements EncryptionOperation {
    private KeyManagement keyManagement;
    public EncryptionManagement(){
        keyManagement=new KeyManagement();
    }

    @Override
    public String decryptFile(File inputFile,File outputFile,File pubKey,InputStream secKeyFile,char[] pass)throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        PGPSecretKey pgpSecretKey;
        InputStream in=PGPUtil.getDecoderStream(new FileInputStream(inputFile));
        JcaPGPObjectFactory pgpObjectFactory;
        PGPObjectFactory objectFactory=new PGPObjectFactory(in,new BcKeyFingerprintCalculator());
        Object object=objectFactory.nextObject();
        PGPEncryptedDataList encryptedDataList;
        if(object instanceof PGPEncryptedDataList){
            encryptedDataList=(PGPEncryptedDataList)object;
        }else{
            encryptedDataList=(PGPEncryptedDataList)objectFactory.nextObject();
        }
        Iterator<PGPPublicKeyEncryptedData> itt = encryptedDataList.getEncryptedDataObjects();
        PGPPrivateKey sKey = null;
        PGPPublicKeyEncryptedData encP = null;

        while (sKey == null && itt.hasNext()) {
            encP = itt.next();
            pgpSecretKey = keyManagement.getSecretKey(secKeyFile, encP.getKeyID());
            sKey = pgpSecretKey.extractPrivateKey(new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass));
        }
        if (sKey == null) {
            throw new IllegalArgumentException("Secret key for message not found.");
        }
        InputStream clear = encP.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));
        pgpObjectFactory=new JcaPGPObjectFactory(clear);
        PGPCompressedData c1 = (PGPCompressedData) pgpObjectFactory.nextObject();
        pgpObjectFactory=new JcaPGPObjectFactory(c1.getDataStream());
        PGPLiteralData ld = (PGPLiteralData) pgpObjectFactory.nextObject();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        InputStream inLd = ld.getDataStream();

        int ch;
        while ((ch = inLd.read()) >= 0) {
            bOut.write(ch);
        }
        OutputStream out=new FileOutputStream(outputFile);
        bOut.writeTo(out);
        out.close();

        String toRuturn=readFile(outputFile.getPath());
        return toRuturn;
    }
    private String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
    @Override
    public  void encryptFile(File outputFile, File inputFile,File pubKeyFile) throws Exception {
        OutputStream out=new FileOutputStream(outputFile);
        String fileName=inputFile.getPath();
        PGPPublicKey encKey=keyManagement.getPublicKey(pubKeyFile);
        Security.addProvider(new BouncyCastleProvider());

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

        PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(fileName));

        comData.close();

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.TRIPLE_DES).setSecureRandom(new SecureRandom()));

        cPk.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(encKey));

        byte[] bytes = bOut.toByteArray();

        OutputStream cOut = cPk.open(out, bytes.length);

        cOut.write(bytes);

        cOut.close();

        out.close();
    }

}

