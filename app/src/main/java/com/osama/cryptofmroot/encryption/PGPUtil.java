/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofmroot.encryption;

import com.osama.cryptofmroot.filemanager.utils.SharedData;

import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.bcpg.ArmoredInputStream;
import org.spongycastle.bcpg.HashAlgorithmTags;
import org.spongycastle.bcpg.MPInteger;
import org.spongycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.util.encoders.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Date;

/**
 * PGP utilities.
 */
public class PGPUtil
    implements HashAlgorithmTags
{
    private    static String    defProvider = "SC";

    /**
     * Return the JCA/JCE provider that will be used by factory classes in situations where a
     * provider must be determined on the fly.
     *
     * @return the name of the default provider.
     * @deprecated unused
     */
    public static String getDefaultProvider()
    {
        // TODO: no longer used.
        return defProvider;
    }

    /**
     * Set the provider to be used by the package when it is necessary to find one on the fly.
     *
     * @param provider the name of the JCA/JCE provider to use by default.
     * @deprecated unused
     */
    public static void setDefaultProvider(
        String    provider)
    {
        defProvider = provider;
    }

    static MPInteger[] dsaSigToMpi(
        byte[] encoding)
        throws PGPException
    {
        ASN1InputStream aIn = new ASN1InputStream(encoding);

        ASN1Integer i1;
        ASN1Integer i2;

        try
        {
            ASN1Sequence s = (ASN1Sequence)aIn.readObject();

            i1 = (ASN1Integer)s.getObjectAt(0);
            i2 = (ASN1Integer)s.getObjectAt(1);
        }
        catch (IOException e)
        {
            throw new PGPException("exception encoding signature", e);
        }

        MPInteger[] values = new MPInteger[2];

        values[0] = new MPInteger(i1.getValue());
        values[1] = new MPInteger(i2.getValue());

        return values;
    }

    /**
     * Generates a random key for a {@link SymmetricKeyAlgorithmTags symmetric encryption algorithm}
     * .
     *
     * @param algorithm the symmetric key algorithm identifier.
     * @param random a source of random data.
     * @return a key of the length required by the specified encryption algorithm.
     * @throws PGPException if the encryption algorithm is unknown.
     */
    public static byte[] makeRandomKey(
        int             algorithm,
        SecureRandom    random)
        throws PGPException
    {
        int        keySize = 0;

        switch (algorithm)
        {
        case SymmetricKeyAlgorithmTags.TRIPLE_DES:
            keySize = 192;
            break;
        case SymmetricKeyAlgorithmTags.IDEA:
            keySize = 128;
            break;
        case SymmetricKeyAlgorithmTags.CAST5:
            keySize = 128;
            break;
        case SymmetricKeyAlgorithmTags.BLOWFISH:
            keySize = 128;
            break;
        case SymmetricKeyAlgorithmTags.SAFER:
            keySize = 128;
            break;
        case SymmetricKeyAlgorithmTags.DES:
            keySize = 64;
            break;
        case SymmetricKeyAlgorithmTags.AES_128:
            keySize = 128;
            break;
        case SymmetricKeyAlgorithmTags.AES_192:
            keySize = 192;
            break;
        case SymmetricKeyAlgorithmTags.AES_256:
            keySize = 256;
            break;
        case SymmetricKeyAlgorithmTags.CAMELLIA_128:
            keySize = 128;
            break;
        case SymmetricKeyAlgorithmTags.CAMELLIA_192:
            keySize = 192;
            break;
        case SymmetricKeyAlgorithmTags.CAMELLIA_256:
            keySize = 256;
            break;
        case SymmetricKeyAlgorithmTags.TWOFISH:
            keySize = 256;
            break;
        default:
            throw new PGPException("unknown symmetric algorithm: " + algorithm);
        }

        byte[]    keyBytes = new byte[(keySize + 7) / 8];

        random.nextBytes(keyBytes);

        return keyBytes;
    }

    /**
     * Write out the contents of the provided file as a literal data packet.
     *
     * @param out the stream to write the literal data to.
     * @param fileType the {@link PGPLiteralData} type to use for the file data.
     * @param file the file to write the contents of.
     *
     * @throws IOException if an error occurs reading the file or writing to the output stream.
     */
    public static void writeFileToLiteralData(
        OutputStream    out,
        char            fileType,
        File            file)
        throws IOException
    {
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(out, fileType, file);
        pipeFileContents(file, pOut, 4096);
    }

    /**
     * Write out the contents of the provided file as a literal data packet in partial packet
     * format.
     *
     * @param out the stream to write the literal data to.
     * @param fileType the {@link PGPLiteralData} type to use for the file data.
     * @param file the file to write the contents of.
     * @param buffer buffer to be used to chunk the file into partial packets.
     * @see PGPLiteralDataGenerator#open(OutputStream, char, String, Date, byte[]).
     *
     * @throws IOException if an error occurs reading the file or writing to the output stream.
     */
    public static void writeFileToLiteralData(
        OutputStream    out,
        char            fileType,
        File            file,
        byte[]          buffer)
        throws IOException
    {
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(out, fileType, file.getName(), new Date(file.lastModified()), buffer);
        pipeFileContents(file, pOut, buffer.length);
    }

    private static void pipeFileContents(File file, OutputStream pOut, int bufSize) throws IOException
    {
        FileInputStream in = new FileInputStream(file);
        byte[] buf = new byte[bufSize];

        int len;
        while ((len = in.read(buf)) > 0)
        {
            //if user cancel the task break the pipe
            if(SharedData.IS_TASK_CANCELED){
                throw new IOException("Operation Canceled");
            }
            pOut.write(buf, 0, len);
        }

        pOut.close();
        in.close();
    }
    public static void pipeDocumentFileContents(InputStream in,OutputStream pOut,int bufSize) throws Exception{
        byte[] buf = new byte[bufSize];

        int len;
        while ((len = in.read(buf)) > 0)
        {
            //if user cancel the task break the pipe
            if(SharedData.IS_TASK_CANCELED){
                throw new IOException("Operation Canceled");
            }
            pOut.write(buf, 0, len);
        }

        pOut.close();
        in.close();
    }

    private static final int READ_AHEAD = 60;

    private static boolean isPossiblyBase64(
        int    ch)
    {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9') || (ch == '+') || (ch == '/')
                || (ch == '\r') || (ch == '\n');
    }

    /**
     * Obtains a stream that can be used to read PGP data from the provided stream.
     * <p>
     * If the initial bytes of the underlying stream are binary PGP encodings, then the stream will
     * be returned directly, otherwise an {@link ArmoredInputStream} is used to wrap the provided
     * stream and remove ASCII-Armored encoding.
     * </p>
     * @param in the stream to be checked and possibly wrapped.
     * @return a stream that will return PGP binary encoded data.
     * @throws IOException if an error occurs reading the stream, or initalising the
     *             {@link ArmoredInputStream}.
     */
    public static InputStream getDecoderStream(
        InputStream    in)
        throws IOException
    {
        if (!in.markSupported())
        {
            in = new BufferedInputStreamExt(in);
        }

        in.mark(READ_AHEAD);

        int    ch = in.read();


        if ((ch & 0x80) != 0)
        {
            in.reset();

            return in;
        }
        else
        {
            if (!isPossiblyBase64(ch))
            {
                in.reset();

                return new ArmoredInputStream(in);
            }

            byte[]  buf = new byte[READ_AHEAD];
            int     count = 1;
            int     index = 1;

            buf[0] = (byte)ch;
            while (count != READ_AHEAD && (ch = in.read()) >= 0)
            {
                if (!isPossiblyBase64(ch))
                {
                    in.reset();

                    return new ArmoredInputStream(in);
                }

                if (ch != '\n' && ch != '\r')
                {
                    buf[index++] = (byte)ch;
                }

                count++;
            }

            in.reset();

            //
            // nothing but new lines, little else, assume regular armoring
            //
            if (count < 4)
            {
                return new ArmoredInputStream(in);
            }

            //
            // test our non-blank data
            //
            byte[]    firstBlock = new byte[8];

            System.arraycopy(buf, 0, firstBlock, 0, firstBlock.length);

            byte[]    decoded = Base64.decode(firstBlock);

            //
            // it's a base64 PGP block.
            //
            if ((decoded[0] & 0x80) != 0)
            {
                return new ArmoredInputStream(in, false);
            }

            return new ArmoredInputStream(in);
        }
    }

    static class BufferedInputStreamExt extends BufferedInputStream
    {
        BufferedInputStreamExt(InputStream input)
        {
            super(input);
        }

        public synchronized int available() throws IOException
        {
            int result = super.available();
            if (result < 0)
            {
                result = Integer.MAX_VALUE;
            }
            return result;
        }
    }
}
