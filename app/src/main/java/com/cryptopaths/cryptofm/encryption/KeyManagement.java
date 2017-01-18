package com.cryptopaths.cryptofm.encryption;

import android.util.Log;

import com.cryptopaths.cryptolib.org.spongycastle.bcpg.HashAlgorithmTags;
import com.cryptopaths.cryptolib.org.spongycastle.bcpg.SymmetricKeyAlgorithmTags;
import com.cryptopaths.cryptolib.org.spongycastle.bcpg.sig.Features;
import com.cryptopaths.cryptolib.org.spongycastle.bcpg.sig.KeyFlags;
import com.cryptopaths.cryptolib.org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import com.cryptopaths.cryptolib.org.spongycastle.crypto.params.RSAKeyGenerationParameters;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPEncryptedData;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPException;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPKeyPair;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPKeyRingGenerator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPrivateKey;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPublicKey;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPPublicKeyRing;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSecretKey;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSignature;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.PGPUtil;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.jcajce.JcaPGPSecretKeyRingCollection;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.PBESecretKeyEncryptor;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.PGPDigestCalculator;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.bc.BcPGPKeyPair;
import com.cryptopaths.cryptolib.org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by osama on 10/8/16.
 */
public class KeyManagement implements KeyOperations {
    @Override
    public PGPKeyRingGenerator generateKey(String email, char[] password) throws Exception {
        RSAKeyPairGenerator rsaKeyPairGenerator=new RSAKeyPairGenerator();
        rsaKeyPairGenerator.init(new RSAKeyGenerationParameters(BigInteger.valueOf(0x10001),new SecureRandom(),4096,12));
        PGPKeyPair keyPair=new BcPGPKeyPair(PGPPublicKey.RSA_SIGN,rsaKeyPairGenerator.generateKeyPair(),new Date());
        PGPKeyPair enKeyPair=new BcPGPKeyPair(PGPPublicKey.RSA_ENCRYPT,rsaKeyPairGenerator.generateKeyPair(),new Date());

        // Add a self-signature on the id
        PGPSignatureSubpacketGenerator signhashgen =
                new PGPSignatureSubpacketGenerator();

        // Add signed metadata on the signature.
        // 1) Declare its purpose
        signhashgen.setKeyFlags
                (false, KeyFlags.SIGN_DATA|KeyFlags.CERTIFY_OTHER);
        // 2) Set preferences for secondary crypto algorithms to use
        //    when sending messages to this key.
        signhashgen.setPreferredSymmetricAlgorithms
                (false, new int[] {
                        SymmetricKeyAlgorithmTags.AES_256,
                        SymmetricKeyAlgorithmTags.AES_192,
                        SymmetricKeyAlgorithmTags.AES_128
                });
        signhashgen.setPreferredHashAlgorithms
                (false, new int[] {
                        HashAlgorithmTags.SHA256,
                        HashAlgorithmTags.SHA1,
                        HashAlgorithmTags.SHA384,
                        HashAlgorithmTags.SHA512,
                        HashAlgorithmTags.SHA224,
                });
        // 3) Request senders add additional checksums to the
        //    message (useful when verifying unsigned messages.)
        signhashgen.setFeature
                (false, Features.FEATURE_MODIFICATION_DETECTION);

        // Create a signature on the encryption subkey.
        PGPSignatureSubpacketGenerator enchashgen =
                new PGPSignatureSubpacketGenerator();
        // Add metadata to declare its purpose
        enchashgen.setKeyFlags
                (false, KeyFlags.ENCRYPT_COMMS|KeyFlags.ENCRYPT_STORAGE);

        // Objects used to encrypt the secret key.
        PGPDigestCalculator sha1Calc =
                new BcPGPDigestCalculatorProvider()
                        .get(HashAlgorithmTags.SHA1);
        PGPDigestCalculator sha256Calc =
                new BcPGPDigestCalculatorProvider()
                        .get(HashAlgorithmTags.SHA256);


        PBESecretKeyEncryptor pske =
                (new BcPBESecretKeyEncryptorBuilder
                        (PGPEncryptedData.AES_256, sha256Calc, 0xc0))
                        .build(password);

        // Finally, create the keyring itself. The constructor
        // takes parameters that allow it to generate the self
        // signature.
        PGPKeyRingGenerator keyRingGen =
                new PGPKeyRingGenerator
                        (PGPSignature.POSITIVE_CERTIFICATION, keyPair,
                                email, sha1Calc, signhashgen.generate(), null,
                                new BcPGPContentSignerBuilder
                                        (keyPair.getPublicKey().getAlgorithm(),
                                                HashAlgorithmTags.SHA1),
                                pske);

        // Add our encryption subkey, together with its signature.
        keyRingGen.addSubKey
                (enKeyPair, enchashgen.generate(), null);
        return keyRingGen;
    }

    @Override
    public PGPPublicKey getPublicKey(File file) throws Exception {
        InputStream input=new FileInputStream(file);
        JcaPGPPublicKeyRingCollection pgpPub = new JcaPGPPublicKeyRingCollection(PGPUtil.getDecoderStream(input));
        PGPPublicKey pubKey = null;

        @SuppressWarnings("unchecked")
        Iterator<PGPPublicKeyRing> keyRingIterator = pgpPub.getKeyRings();
        while (keyRingIterator.hasNext() && pubKey == null) {
            PGPPublicKeyRing keyRing = keyRingIterator.next();

            @SuppressWarnings("unchecked")
            Iterator<PGPPublicKey> keyIterator = keyRing.getPublicKeys();
            while (keyIterator.hasNext()) {
                PGPPublicKey key = keyIterator.next();

                if (key.isEncryptionKey()) {
                    pubKey = key;
                    break;
                }
            }
        }

        if(pubKey != null) {
            return pubKey;
        }
        else {
            throw new IllegalArgumentException("Can't find encryption key in key ring.");
        }
    }


    @Override
    public PGPSecretKey getSecretKey(InputStream binaryData,long keyID) throws Exception {
        InputStream inputStream=PGPUtil.getDecoderStream(binaryData);
        JcaPGPSecretKeyRingCollection pgpSecretKeyRings=new JcaPGPSecretKeyRingCollection(PGPUtil.getDecoderStream(inputStream));
        PGPSecretKey secretKey=pgpSecretKeyRings.getSecretKey(keyID);
        if(secretKey!=null){
            return secretKey;
        }else{
            return secretKey;
        }

    }
    public PGPPrivateKey findSecretKey(PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass)
            throws PGPException, NoSuchProviderException
    {
        PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
        PGPPrivateKey x;
        if (pgpSecKey == null)
        {
            return null;
        }

        try{
            Log.d("decrypt", "findSecretKey: fixing something");
           x =pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass));
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        return x;
    }
}
