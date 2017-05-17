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

package com.slownet5.pgprootexplorer.filemanager.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.slownet5.pgprootexplorer.CryptoFM;
import com.slownet5.pgprootexplorer.R;

/**
 * Created by tripleheader on 1/23/17.
 * this class be responsible for providing the file icons
 */

public class IconManager {

    public static final Drawable ENCRYPT_ICON = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_encrypted_file);
    public static final Drawable ARCHIVE_ZIP  = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_compress_file);
    public static final Drawable TORRENT_ICON = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_torrent_file);
    public static final Drawable AUDIO_ICON   = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_audio_file);
    public static final Drawable IMAGE_ICON   = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_image_file);
    public static final Drawable TEXT_ICON    = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_text_file);
    public static final Drawable SCRIPT_ICON  = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_code_file);
    public static final Drawable VIDEO_ICON   = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_video_file);
    public static final Drawable BLANK_ICON   = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_unknown_file);
    public static final Drawable PDF          = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_pdf_file);
    public static final Drawable EXCEL        = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_sheet_file);
    public static final Drawable PPT          = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_presentation_file);
    public static final Drawable DOC          = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_docs_file);
    public static final Drawable APK          = ContextCompat.getDrawable(CryptoFM.getContext(),R.drawable.ic_apk_file);

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