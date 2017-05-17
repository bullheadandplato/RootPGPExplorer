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

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.slownet5.RootTools.containers.Mount;
import com.slownet5.RootTools.containers.Permissions;
import com.slownet5.RootTools.containers.Symlink;

public class InternalVariables
{

    // ----------------------
    // # Internal Variables #
    // ----------------------


    protected static boolean nativeToolsReady = false;
    protected static boolean found = false;
    protected static boolean processRunning = false;

    protected static String[] space;
    protected static String getSpaceFor;
    protected static String busyboxVersion;
    protected static String pid_list = "";
    protected static ArrayList<Mount> mounts;
    protected static ArrayList<Symlink> symlinks;
    protected static String inode = "";
    protected static Permissions permissions;

    // regex to get pid out of ps line, example:
    // root 2611 0.0 0.0 19408 2104 pts/2 S 13:41 0:00 bash
    protected static final String PS_REGEX = "^\\S+\\s+([0-9]+).*$";
    protected static Pattern psPattern;

    static
    {
        psPattern = Pattern.compile(PS_REGEX);
    }
}
