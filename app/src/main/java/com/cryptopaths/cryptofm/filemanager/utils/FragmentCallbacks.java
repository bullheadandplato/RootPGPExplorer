package com.cryptopaths.cryptofm.filemanager.utils;

import com.cryptopaths.cryptofm.filemanager.ui.TabsFragmentOne;

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
