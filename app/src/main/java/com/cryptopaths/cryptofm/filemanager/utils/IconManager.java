package com.cryptopaths.cryptofm.filemanager.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cryptopaths.cryptofm.R;

/**
 * Created by tripleheader on 1/23/17.
 * this class be responsible for providing the file icons
 */

public class IconManager {
    public static Drawable PHOTO_ICON;
    public IconManager(Context context){
        //get the references to all the icons here
        PHOTO_ICON=context.getDrawable(R.drawable.ic_photo);
    }
}
