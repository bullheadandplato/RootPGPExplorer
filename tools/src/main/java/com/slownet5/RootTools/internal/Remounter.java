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
import java.io.IOException;
import java.util.ArrayList;

import com.slownet5.RootShell.execution.Command;
import com.slownet5.RootShell.execution.Shell;
import com.slownet5.RootTools.Constants;
import com.slownet5.RootTools.RootTools;
import com.slownet5.RootTools.containers.Mount;

public class Remounter
{

    //-------------
    //# Remounter #
    //-------------

    /**
     * This will take a path, which can contain the file name as well,
     * and attempt to remount the underlying partition.
     * <p/>
     * For example, passing in the following string:
     * "/system/bin/some/directory/that/really/would/never/exist"
     * will result in /system ultimately being remounted.
     * However, keep in mind that the longer the path you supply, the more work this has to do,
     * and the slower it will run.
     *
     * @param file      file path
     * @param mountType mount type: pass in RO (Read only) or RW (Read Write)
     * @return a <code>boolean</code> which indicates whether or not the partition
     * has been remounted as specified.
     */

    public boolean remount(String file, String mountType)
    {

        //if the path has a trailing slash get rid of it.
        if (file.endsWith("/") && !file.equals("/"))
        {
            file = file.substring(0, file.lastIndexOf("/"));
        }
        //Make sure that what we are trying to remount is in the mount list.
        boolean foundMount = false;

        while (!foundMount)
        {
            try
            {
                for (Mount mount : RootTools.getMounts())
                {
                    RootTools.log(mount.getMountPoint().toString());

                    if (file.equals(mount.getMountPoint().toString()))
                    {
                        foundMount = true;
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                if (RootTools.debugMode)
                {
                    e.printStackTrace();
                }
                return false;
            }
            if (!foundMount)
            {
                try
                {
                    file = (new File(file).getParent());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        Mount mountPoint = findMountPointRecursive(file);

        if (mountPoint != null)
        {

            RootTools.log(Constants.TAG, "Remounting " + mountPoint.getMountPoint().getAbsolutePath() + " as " + mountType.toLowerCase());
            final boolean isMountMode = mountPoint.getFlags().contains(mountType.toLowerCase());

            if (!isMountMode)
            {
                //grab an instance of the internal class
                try
                {
                    Command command = new Command(0,
                            true,
                            "busybox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "toolbox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "toybox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "mount -o remount," + mountType.toLowerCase() + " " + file,
                            "/system/bin/toolbox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "/system/bin/toybox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath()
                    );
                    Shell.startRootShell().add(command);
                    commandWait(command);

                }
                catch (Exception e)
                {
                }

                mountPoint = findMountPointRecursive(file);
            }

            if (mountPoint != null)
            {
                RootTools.log(Constants.TAG, mountPoint.getFlags() + " AND " + mountType.toLowerCase());
                if (mountPoint.getFlags().contains(mountType.toLowerCase()))
                {
                    RootTools.log(mountPoint.getFlags().toString());
                    return true;
                }
                else
                {
                    RootTools.log(mountPoint.getFlags().toString());
                    return false;
                }
            }
            else
            {
                RootTools.log("mount is null, file was: " + file + " mountType was: " + mountType);
            }
        }
        else
        {
            RootTools.log("mount is null, file was: " + file + " mountType was: " + mountType);
        }

        return false;
    }

    private Mount findMountPointRecursive(String file)
    {
        try
        {
            ArrayList<Mount> mounts = RootTools.getMounts();

            for (File path = new File(file); path != null; )
            {
                for (Mount mount : mounts)
                {
                    if (mount.getMountPoint().equals(path))
                    {
                        return mount;
                    }
                }
            }

            return null;

        }
        catch (IOException e)
        {
            if (RootTools.debugMode)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            if (RootTools.debugMode)
            {
                e.printStackTrace();
            }
        }

        return null;
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
