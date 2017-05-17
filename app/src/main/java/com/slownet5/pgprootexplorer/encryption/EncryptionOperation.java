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

package com.slownet5.pgprootexplorer.encryption;

import java.io.File;
import java.io.InputStream;

/**
 * Created by osama on 10/13/16.
 */

public interface EncryptionOperation {
    boolean encryptFile(File inputFile, File outputFile, File keyFile, Boolean integrityCheck)throws Exception ;
    boolean decryptFile(File inputFile, File outputFile, File pubKeyFile, InputStream secKeyFile, char[] pass)throws Exception;
}
