package com.cryptopaths.cryptofm.encryption;

import org.spongycastle.bcpg.HashAlgorithmTags;
import org.spongycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.spongycastle.bcpg.sig.Features;
import org.spongycastle.bcpg.sig.KeyFlags;
import org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import org.spongycastle.crypto.params.RSAKeyGenerationParameters;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPKeyPair;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import org.spongycastle.openpgp.jcajce.JcaPGPSecretKeyRingCollection;
import org.spongycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.spongycastle.openpgp.operator.PGPDigestCalculator;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.spongycastle.openpgp.operator.bc.BcPGPKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
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
}
