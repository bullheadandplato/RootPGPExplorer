package com.cryptopaths.cryptofm.filemanager.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cryptopaths.cryptofm.CryptoFM;
import com.cryptopaths.cryptofm.R;

/**
 * Created by tripleheader on 1/23/17.
 * this class be responsible for providing the file icons
 */

public class IconManager {
    public static final Drawable PHOTO_ICON= CryptoFM.getContext().getDrawable(R.drawable.ic_application_blank);
    public  Drawable CHECK_CIRCLE_ICON;
    public  Drawable COPY_ICON;
    public  Drawable CUT_CONTENT_ICON;
    public  Drawable DECRYPT_ICON;
    public  Drawable DELETE_ICON;
    public  Drawable ENCRYPT_ICON;
    public  Drawable GRID_VIEW_ICON;
    public  Drawable ITEMS_VIEW_ICON;
    public  Drawable NEXT_PAGE_ICON;
    public  Drawable SELECT_ALL_ICON;
    public  Drawable NO_FILE_ICON;
    public  Drawable ARCHIVE_ZIP;
    public  Drawable TORRENT_ICON;
    public  Drawable AUDIO_ICON;
    public  Drawable IMAGE_ICON;
    public  Drawable TEXT_ICON;
    public  Drawable SCRIPT_ICON;
    public  Drawable VIDEO_ICON;
    public  Drawable BLANK_ICON;
    public  Drawable PDF;

    public IconManager(Context context){
        //get the references to all the icons here
        COPY_ICON          =context.getDrawable(R.drawable.ic_copy);
        DELETE_ICON        =context.getDrawable(R.drawable.ic_delete);
        PDF                =context.getDrawable(R.drawable.ic_application_pdf);
        DECRYPT_ICON       =context.getDrawable(R.drawable.ic_decrypt);
        ENCRYPT_ICON       =context.getDrawable(R.drawable.ic_encrypted);
        NO_FILE_ICON       =context.getDrawable(R.drawable.nofiles_image);
        GRID_VIEW_ICON     =context.getDrawable(R.drawable.ic_grid_view);
        NEXT_PAGE_ICON     =context.getDrawable(R.drawable.ic_next_page);
        ITEMS_VIEW_ICON    =context.getDrawable(R.drawable.ic_items_view);
        SELECT_ALL_ICON    =context.getDrawable(R.drawable.ic_select_all);
        TEXT_ICON          =context.getDrawable(R.drawable.ic_application_text);
        AUDIO_ICON         =context.getDrawable(R.drawable.ic_application_audio);
        BLANK_ICON         =context.getDrawable(R.drawable.ic_application_blank);
        CUT_CONTENT_ICON   =context.getDrawable(R.drawable.ic_cut_content);
        VIDEO_ICON         =context.getDrawable(R.drawable.ic_application_video);
        IMAGE_ICON         =context.getDrawable(R.drawable.ic_application_images);
        TORRENT_ICON       =context.getDrawable(R.drawable.ic_application_torrent);
        ARCHIVE_ZIP        =context.getDrawable(R.drawable.ic_application_archive_zip);
        SCRIPT_ICON        =context.getDrawable(R.drawable.ic_application_script_blank);
        CHECK_CIRCLE_ICON  =context.getDrawable(R.drawable.ic_check_circle_white_48dp);

    }
}