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

package com.osama.cryptofmroot.filemanager.listview;

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
}
