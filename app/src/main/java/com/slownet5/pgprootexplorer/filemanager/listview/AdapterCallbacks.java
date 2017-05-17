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

package com.slownet5.pgprootexplorer.filemanager.listview;

/**
 * Created by tripleheader on 1/21/17.
 * necessary callback which will be invoked upon item selection
 */

public interface AdapterCallbacks {
    void onLongClick();
    void incrementSelectionCount();
    void decrementSelectionCount();
    void changeTitle(String path);
    void showNoFilesFragment();
    void removeNoFilesFragment();
    void animateForward(String path);
    void selectedFileType(boolean isFolder);
}
