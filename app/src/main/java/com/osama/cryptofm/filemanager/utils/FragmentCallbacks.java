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

package com.osama.cryptofm.filemanager.utils;

import com.osama.cryptofm.filemanager.ui.TabsFragmentOne;

/**
 * Created by tripleheader on 1/22/17.
 * different callback that will be executed on changes in fragment state
 */

public interface FragmentCallbacks {
    void init();
    void finishActionMode();
    void tellNoFiles();
    void setCurrentFragment(TabsFragmentOne fragment, int position);
}
