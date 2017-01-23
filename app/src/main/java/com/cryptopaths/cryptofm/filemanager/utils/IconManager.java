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
    public static Drawable CHECK_CIRCLE_ICON;
    public static Drawable COPY_ICON;
    public static Drawable CUT_CONTENT_ICON;
    public static Drawable DECRYPT_ICON;
    public static Drawable DELETE_ICON;
    public static Drawable ENCRYPT_ICON;
    public static Drawable FOLDER_WHITE_ICON;
    public static Drawable GRID_VIEW_ICON;
    public static Drawable INSERT_DRIVE_FILE_WHITE_ICON;
    public static Drawable ITEMS_VIEW_ICON;
    public static Drawable NEXT_PAGE_ICON;
    public static Drawable SELECT_ALL_ICON;
    public static Drawable VIEW_PAGER_ICON_ONE;
    public static Drawable VIEW_PAGER_ICON_TWO;
    public static Drawable VIEW_PAGER_ICON_THREE;
    public static Drawable VIEW_PAGER_ICON_FOUR;
    public static Drawable VISIBILITY_BLACK_ICON;
    public static Drawable VISIBILITY_OFF_BLACK_ICON;
    public static Drawable WATCH_LATER_ICON;
    public static Drawable LOGO_ICON;
    public static Drawable NO_FILE_ICON;
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

    }
}