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

package com.osama.cryptofmroot.filemanager.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.R;

/**
 * Created by tripleheader on 1/23/17.
 * this class be responsible for providing the file icons
 */

public class IconManager {

    public static final  Drawable ENCRYPT_ICON= CryptoFM.getContext().getDrawable(R.drawable.ic_encrypted);
    public static final   Drawable ARCHIVE_ZIP=CryptoFM.getContext().getDrawable(R.drawable.ic_application_archive_zip);
    public  static final  Drawable TORRENT_ICON=CryptoFM.getContext().getDrawable(R.drawable.ic_application_torrent);
    public   static final Drawable AUDIO_ICON=CryptoFM.getContext().getDrawable(R.drawable.ic_application_audio);
    public  static final  Drawable IMAGE_ICON=CryptoFM.getContext().getDrawable(R.drawable.ic_application_images);
    public  static final  Drawable TEXT_ICON=CryptoFM.getContext().getDrawable(R.drawable.ic_application_text);
    public  static final  Drawable SCRIPT_ICON=CryptoFM.getContext().getDrawable(R.drawable.ic_application_script_blank);
    public  static final  Drawable VIDEO_ICON=CryptoFM.getContext().getDrawable(R.drawable.ic_application_video);
    public  static final  Drawable BLANK_ICON=CryptoFM.getContext().getDrawable(R.drawable.ic_application_blank);
    public static final   Drawable PDF=CryptoFM.getContext().getDrawable(R.drawable.ic_application_pdf);
    public static final Drawable EXCEL=CryptoFM.getContext().getDrawable(R.drawable.ic_application_table);
    public static final Drawable PPT=CryptoFM.getContext().getDrawable(R.drawable.ic_application_presentation);
    public static final Drawable DOC=CryptoFM.getContext().getDrawable(R.drawable.ic_application_word);
    public static final Drawable APK=CryptoFM.getContext().getDrawable(R.drawable.ic_application_apk);

   /* public IconManager(Context context){
        //get the references to all the icons here
        PDF                = context.getDrawable(R.drawable.ic_application_pdf);
        ENCRYPT_ICON       = context.getDrawable(R.drawable.ic_encrypted);
        TEXT_ICON          = context.getDrawable(R.drawable.ic_application_text);
        AUDIO_ICON         = context.getDrawable(R.drawable.ic_application_audio);
        BLANK_ICON         = context.getDrawable(R.drawable.ic_application_blank);
        VIDEO_ICON         = context.getDrawable(R.drawable.ic_application_video);
        IMAGE_ICON         = context.getDrawable(R.drawable.ic_application_images);
        TORRENT_ICON       = context.getDrawable(R.drawable.ic_application_torrent);
        ARCHIVE_ZIP        = context.getDrawable(R.drawable.ic_application_archive_zip);
        SCRIPT_ICON        = context.getDrawable(R.drawable.ic_application_script_blank);
        EXCEL              = context.getDrawable(R.drawable.ic_application_table);
        PPT                = context.getDrawable(R.drawable.ic_application_presentation);
        DOC                = context.getDrawable(R.drawable.ic_application_word);
        APK                = context.getDrawable(R.drawable.ic_application_apk);

    }*/
}