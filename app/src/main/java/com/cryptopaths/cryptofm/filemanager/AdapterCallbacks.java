package com.cryptopaths.cryptofm.filemanager;

/**
 * Created by tripleheader on 1/21/17.
 * necessary callback which will be invoked upon item selection
 */

public interface AdapterCallbacks {
    void onLongClick();
    void incrementSelectionCount();
    void decrementSelectionCount();
    void changeTitle(String path);
}
