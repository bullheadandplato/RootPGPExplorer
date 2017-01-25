package com.cryptopaths.cryptofm.filemanager.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cryptopaths.cryptofm.R;

/**
 * Created by tripleheader on 1/23/17.
 * this class be responsible for providing the file icons
 */

public class IconManager {
    public  Drawable PHOTO_ICON;
    public  Drawable CHECK_CIRCLE_ICON;
    public  Drawable COPY_ICON;
    public  Drawable CUT_CONTENT_ICON;
    public  Drawable DECRYPT_ICON;
    public  Drawable DELETE_ICON;
    public  Drawable ENCRYPT_ICON;
    public  Drawable FOLDER_WHITE_ICON;
    public  Drawable GRID_VIEW_ICON;
    public  Drawable INSERT_DRIVE_FILE_WHITE_ICON;
    public  Drawable ITEMS_VIEW_ICON;
    public  Drawable NEXT_PAGE_ICON;
    public  Drawable SELECT_ALL_ICON;
    public  Drawable VIEW_PAGER_ICON_ONE;
    public  Drawable VIEW_PAGER_ICON_TWO;
    public  Drawable VIEW_PAGER_ICON_THREE;
    public  Drawable VIEW_PAGER_ICON_FOUR;
    public  Drawable VISIBILITY_BLACK_ICON;
    public  Drawable VISIBILITY_OFF_BLACK_ICON;
    public  Drawable WATCH_LATER_ICON;
    public  Drawable LOGO_ICON;
    public  Drawable NO_FILE_ICON;
    public  Drawable ARCHIVE_ZIP;
    public  Drawable TORRENT_ICON;
    public  Drawable AUDIO_ICON;
    public  Drawable IMAGE_ICON;
    public  Drawable TEXT_ICON;
    public  Drawable SCRIPT_ICON;
    public  Drawable VIDEO_ICON;
    public  Drawable BLANK_ICON;

    public IconManager(Context context){
        //get the references to all the icons here
        PHOTO_ICON=context.getDrawable(R.drawable.ic_photo);
        CHECK_CIRCLE_ICON=context.getDrawable(R.drawable.ic_check_circle_white_48dp);
        COPY_ICON=context.getDrawable(R.drawable.ic_copy);
        CUT_CONTENT_ICON=context.getDrawable(R.drawable.ic_cut_content);
        DECRYPT_ICON=context.getDrawable(R.drawable.ic_decrypt);
        DELETE_ICON=context.getDrawable(R.drawable.ic_delete);
        ENCRYPT_ICON=context.getDrawable(R.drawable.ic_encrypt);
        FOLDER_WHITE_ICON=context.getDrawable(R.drawable.ic_folder_white_48dp);
        GRID_VIEW_ICON=context.getDrawable(R.drawable.ic_grid_view);
        INSERT_DRIVE_FILE_WHITE_ICON=context.getDrawable(R.drawable.ic_insert_drive_file_white_48dp);
        ITEMS_VIEW_ICON=context.getDrawable(R.drawable.ic_items_view);
        NEXT_PAGE_ICON=context.getDrawable(R.drawable.ic_next_page);
        SELECT_ALL_ICON=context.getDrawable(R.drawable.ic_select_all);
        VIEW_PAGER_ICON_ONE=context.getDrawable(R.drawable.ic_view_pager_image1);
        VIEW_PAGER_ICON_TWO=context.getDrawable(R.drawable.ic_view_pager_image2);
        VIEW_PAGER_ICON_THREE=context.getDrawable(R.drawable.ic_view_pager_image3);
        VIEW_PAGER_ICON_FOUR=context.getDrawable(R.drawable.ic_view_pager_image4);
        VISIBILITY_BLACK_ICON=context.getDrawable(R.drawable.ic_visibility_black_48dp);
        VISIBILITY_OFF_BLACK_ICON=context.getDrawable(R.drawable.ic_visibility_off_black_48dp);
        WATCH_LATER_ICON=context.getDrawable(R.drawable.ic_watch_later_black_24dp);
        LOGO_ICON=context.getDrawable(R.drawable.logo);
        NO_FILE_ICON=context.getDrawable(R.drawable.nofiles_image);
        ARCHIVE_ZIP=context.getDrawable(R.drawable.ic_application_archive_zip);
        TORRENT_ICON=context.getDrawable(R.drawable.ic_application_torrent);
        AUDIO_ICON=context.getDrawable(R.drawable.ic_application_audio);
        IMAGE_ICON=context.getDrawable(R.drawable.ic_application_images);
        TEXT_ICON=context.getDrawable(R.drawable.ic_application_text);
        SCRIPT_ICON=context.getDrawable(R.drawable.ic_application_script_blank);
        VIDEO_ICON=context.getDrawable(R.drawable.ic_application_video);
        BLANK_ICON=context.getDrawable(R.drawable.ic_application_blank);
    }
}