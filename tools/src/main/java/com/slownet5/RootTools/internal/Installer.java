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

package com.slownet5.RootTools.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

import com.slownet5.RootShell.execution.Command;
import com.slownet5.RootShell.execution.Shell;
import com.slownet5.RootTools.RootTools;

import android.content.Context;

class Installer
{

    //-------------
    //# Installer #
    //-------------

    static final String LOG_TAG = "RootTools::Installer";

    static final String BOGUS_FILE_NAME = "bogus";

    Context context;
    String filesPath;

    public Installer(Context context)
            throws IOException
    {

        this.context = context;
        this.filesPath = context.getFilesDir().getCanonicalPath();
    }

    /**
     * This method can be used to unpack a binary from the raw resources folder and store it in
     * /data/data/app.package/files/
     * This is typically useful if you provide your own C- or C++-based binary.
     * This binary can then be executed using sendShell() and its full path.
     *
     * @param sourceId resource id; typically <code>R.raw.id</code>
     * @param destName destination file name; appended to /data/data/app.package/files/
     * @param mode     chmod value for this file
     * @return a <code>boolean</code> which indicates whether or not we were
     * able to create the new file.
     */
    protected boolean installBinary(int sourceId, String destName, String mode)
    {
        File mf = new File(filesPath + File.separator + destName);
        if (!mf.exists() ||
                !getFileSignature(mf).equals(
                        getStreamSignature(
                                context.getResources().openRawResource(sourceId))
                ))
        {
            Log.e(LOG_TAG, "Installing a new version of binary: " + destName);
            // First, does our files/ directory even exist?
            // We cannot wait for android to lazily create it as we will soon
            // need it.
            try
            {
                FileInputStream fis = context.openFileInput(BOGUS_FILE_NAME);
                fis.close();
            }
            catch (FileNotFoundException e)
            {
                FileOutputStream fos = null;
                try
                {
                    fos = context.openFileOutput("bogus", Context.MODE_PRIVATE);
                    fos.write("justcreatedfilesdirectory".getBytes());
                }
                catch (Exception ex)
                {
                    if (RootTools.debugMode)
                    {
                        Log.e(LOG_TAG, ex.toString());
                    }
                    return false;
                }
                finally
                {
                    if (null != fos)
                    {
                        try
                        {
                            fos.close();
                            context.deleteFile(BOGUS_FILE_NAME);
                        }
                        catch (IOException e1)
                        {
                        }
                    }
                }
            }
            catch (IOException ex)
            {
                if (RootTools.debugMode)
                {
                    Log.e(LOG_TAG, ex.toString());
                }
                return false;
            }

            // Only now can we start creating our actual file
            InputStream iss = context.getResources().openRawResource(sourceId);
            ReadableByteChannel rfc = Channels.newChannel(iss);
            FileOutputStream oss = null;
            try
            {
                oss = new FileOutputStream(mf);
                FileChannel ofc = oss.getChannel();
                long pos = 0;
                try
                {
                    long size = iss.available();
                    while ((pos += ofc.transferFrom(rfc, pos, size - pos)) < size)
                    {
                        ;
                    }
                }
                catch (IOException ex)
                {
                    if (RootTools.debugMode)
                    {
                        Log.e(LOG_TAG, ex.toString());
                    }
                    return false;
                }
            }
            catch (FileNotFoundException ex)
            {
                if (RootTools.debugMode)
                {
                    Log.e(LOG_TAG, ex.toString());
                }
                return false;
            }
            finally
            {
                if (oss != null)
                {
                    try
                    {
                        oss.flush();
                        oss.getFD().sync();
                        oss.close();
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
            try
            {
                iss.close();
            }
            catch (IOException ex)
            {
                if (RootTools.debugMode)
                {
                    Log.e(LOG_TAG, ex.toString());
                }
                return false;
            }

            try
            {
                Command command = new Command(0, false, "chmod " + mode + " " + filesPath + File.separator + destName);
                Shell.startRootShell().add(command);
                commandWait(command);

            }
            catch (Exception e)
            {
            }
        }
        return true;
    }

    protected boolean isBinaryInstalled(String destName)
    {
        boolean installed = false;
        File mf = new File(filesPath + File.separator + destName);
        if (mf.exists())
        {
            installed = true;
            // TODO: pass mode as argument and check it matches
        }
        return installed;
    }

    protected String getFileSignature(File f)
    {
        String signature = "";
        try
        {
            signature = getStreamSignature(new FileInputStream(f));
        }
        catch (FileNotFoundException ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }
        return signature;
    }

    /*
     * Note: this method will close any string passed to it
     */
    protected String getStreamSignature(InputStream is)
    {
        String signature = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream dis = new DigestInputStream(is, md);
            byte[] buffer = new byte[4096];
            while (-1 != dis.read(buffer))
            {
                ;
            }
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < digest.length; i++)
            {
                sb.append(Integer.toHexString(digest[i] & 0xFF));
            }

            signature = sb.toString();
        }
        catch (IOException ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }
        catch (NoSuchAlgorithmException ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
            }
        }
        return signature;
    }

    private void commandWait(Command cmd)
    {
        synchronized (cmd)
        {
            try
            {
                if (!cmd.isFinished())
                {
                    cmd.wait(2000);
                }
            }
            catch (InterruptedException ex)
            {
                Log.e(LOG_TAG, ex.toString());
            }
        }
    }
}
