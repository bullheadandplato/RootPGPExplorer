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

package com.slownet5.RootTools.containers;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Mount
{
    final File mDevice;
    final File mMountPoint;
    final String mType;
    final Set<String> mFlags;

    public Mount(File device, File path, String type, String flagsStr)
    {
        mDevice = device;
        mMountPoint = path;
        mType = type;
        mFlags = new LinkedHashSet<String>(Arrays.asList(flagsStr.split(",")));
    }

    public File getDevice()
    {
        return mDevice;
    }

    public File getMountPoint()
    {
        return mMountPoint;
    }

    public String getType()
    {
        return mType;
    }

    public Set<String> getFlags()
    {
        return mFlags;
    }

    @Override
    public String toString()
    {
        return String.format("%s on %s type %s %s", mDevice, mMountPoint, mType, mFlags);
    }

}
