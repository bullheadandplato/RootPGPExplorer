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

import java.io.IOException;

import com.slownet5.RootShell.execution.Command;
import com.slownet5.RootShell.execution.Shell;
import com.slownet5.RootTools.RootTools;

import android.content.Context;
import android.util.Log;

public class Runner extends Thread
{

    private static final String LOG_TAG = "RootTools::Runner";

    Context context;
    String binaryName;
    String parameter;

    public Runner(Context context, String binaryName, String parameter)
    {
        this.context = context;
        this.binaryName = binaryName;
        this.parameter = parameter;
    }

    public void run()
    {
        String privateFilesPath = null;
        try
        {
            privateFilesPath = context.getFilesDir().getCanonicalPath();
        }
        catch (IOException e)
        {
            if (RootTools.debugMode)
            {
                Log.e(LOG_TAG, "Problem occured while trying to locate private files directory!");
            }
            e.printStackTrace();
        }
        if (privateFilesPath != null)
        {
            try
            {
                Command command = new Command(0, false, privateFilesPath + "/" + binaryName + " " + parameter);
                Shell.startRootShell().add(command);
                commandWait(command);

            }
            catch (Exception e)
            {
            }
        }
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
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
