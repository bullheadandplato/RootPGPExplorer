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

package com.slownet5.RootShellTests;

import com.slownet5.RootShell.containers.RootClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RootClass.Candidate
public class NativeJavaClass
{

    public NativeJavaClass(RootClass.RootArgs args)
    {
        System.out.println("NativeJavaClass says: oh hi there.");
        String p = "/data/data/com.android.browser/cache";
        File f = new File(p);
        String[] fl = f.list();
        if (fl != null)
        {
            System.out.println("Look at all the stuff in your browser's cache:");
            for (String af : fl)
            {
                System.out.println("-" + af);
            }
            System.out.println("Leaving my mark for posterity...");
            File f2 = new File(p + "/rootshell_was_here");
            try
            {
                FileWriter filewriter = new FileWriter(f2);
                BufferedWriter out = new BufferedWriter(filewriter);
                out.write("This is just a file created using RootShell's Sanity check tools..\n");
                out.close();
                System.out.println("Done!");
            }
            catch (IOException e)
            {
                System.out.println("...and I failed miserably.");
                e.printStackTrace();
            }

        }
    }

}
