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

package com.slownet5.pgprootexplorer.tasks;

import java.io.File;
import java.io.IOException;

/**
 * Created by tripleheader on 12/26/16.
 * the file operation tasks needs to performed with the files
 */

public class TasksFileUtils {
    public static File getFile(String path) throws IOException{
        // called to get destination file it must have to be created.
        File temp=new File(path);
        if(!temp.exists()){
            if(temp.createNewFile()){
                return temp;
            }else{
                throw new IOException("cannot create file");
            }
        }else{
            return temp;
        }
    }

}
