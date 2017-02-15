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

package com.osama.cryptofm.encryption;

import com.osama.cryptofm.filemanager.utils.SharedData;

import org.spongycastle.util.io.StreamOverflowException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by tripleheader on 2/3/17.
 * basic stream methods
 */

/**
 * Utility methods to assist with stream processing.
 */
public final class Streams {
    private static int BUFFER_SIZE = 4096;

    /**
     * Read stream till EOF is encountered.
     *
     * @param inStr stream to be emptied.
     * @throws IOException in case of underlying IOException.
     */
    public static void drain(InputStream inStr)
            throws IOException {
        byte[] bs = new byte[BUFFER_SIZE];
        while (inStr.read(bs, 0, bs.length) >= 0) {
        }
    }

    /**
     * Read stream fully, returning contents in a byte array.
     *
     * @param inStr stream to be read.
     * @return a byte array representing the contents of inStr.
     * @throws IOException in case of underlying IOException.
     */
    public static byte[] readAll(InputStream inStr)
            throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        pipeAll(inStr, buf);
        return buf.toByteArray();
    }

    /**
     * Read from inStr up to a maximum number of bytes, throwing an exception if more the maximum amount
     * of requested data is available.
     *
     * @param inStr stream to be read.
     * @param limit maximum number of bytes that can be read.
     * @return a byte array representing the contents of inStr.
     * @throws IOException in case of underlying IOException, or if limit is reached on inStr still has data in it.
     */
    public static byte[] readAllLimited(InputStream inStr, int limit)
            throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        pipeAllLimited(inStr, limit, buf);
        return buf.toByteArray();
    }

    /**
     * Fully read in buf's length in data, or up to EOF, whichever occurs first,
     *
     * @param inStr the stream to be read.
     * @param buf   the buffer to be read into.
     * @return the number of bytes read into the buffer.
     * @throws IOException in case of underlying IOException.
     */
    public static int readFully(InputStream inStr, byte[] buf)
            throws IOException {
        return readFully(inStr, buf, 0, buf.length);
    }

    /**
     * Fully read in len's bytes of data into buf, or up to EOF, whichever occurs first,
     *
     * @param inStr the stream to be read.
     * @param buf   the buffer to be read into.
     * @param off   offset into buf to start putting bytes into.
     * @param len   the number of bytes to be read.
     * @return the number of bytes read into the buffer.
     * @throws IOException in case of underlying IOException.
     */
    public static int readFully(InputStream inStr, byte[] buf, int off, int len)
            throws IOException {
        int totalRead = 0;
        while (totalRead < len) {
            int numRead = inStr.read(buf, off + totalRead, len - totalRead);
            if (numRead < 0) {
                break;
            }
            totalRead += numRead;
        }
        return totalRead;
    }

    /**
     * Write the full contents of inStr to the destination stream outStr.
     *
     * @param inStr  source input stream.
     * @param outStr destination output stream.
     * @throws IOException in case of underlying IOException.
     */
    public static void pipeAll(InputStream inStr, OutputStream outStr)
            throws IOException {
        byte[] bs = new byte[BUFFER_SIZE];
        int numRead;
        while ((numRead = inStr.read(bs, 0, bs.length)) >= 0) {
            if(SharedData.IS_TASK_CANCELED){
                throw new IOException("Operation canceled");
            }
            outStr.write(bs, 0, numRead);
        }
    }

    /**
     * Write up to limit bytes of data from inStr to the destination stream outStr.
     *
     * @param inStr  source input stream.
     * @param limit  the maximum number of bytes allowed to be read.
     * @param outStr destination output stream.
     * @throws IOException in case of underlying IOException, or if limit is reached on inStr still has data in it.
     */
    public static long pipeAllLimited(InputStream inStr, long limit, OutputStream outStr)
            throws IOException {
        long total = 0;
        byte[] bs = new byte[BUFFER_SIZE];
        int numRead;
        while ((numRead = inStr.read(bs, 0, bs.length)) >= 0) {
            if (SharedData.IS_TASK_CANCELED) {
                throw new IOException("Operation canceled");
            }
            if ((limit - total) < numRead) {
                throw new StreamOverflowException("Data Overflow");
            }
            total += numRead;
            outStr.write(bs, 0, numRead);
        }
        return total;
    }
}
