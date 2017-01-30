package com.cryptopaths.cryptofm.filemanager.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Shadow on 1/25/2017.
 *
 */

public class MimeType {

    private static IconManager mIcons;

    public MimeType(Context context) {
        if (mIcons == null) {
            mIcons = new IconManager(context);
        }
    }


    public static Drawable getIcon(String fileExtensionOrItems) {

        if (
                        fileExtensionOrItems.equalsIgnoreCase("mp4") ||
                        fileExtensionOrItems.equalsIgnoreCase("mkv") ||
                        fileExtensionOrItems.equalsIgnoreCase("3gp") ||
                        fileExtensionOrItems.equalsIgnoreCase("webm")) {
            return IconManager.PHOTO_ICON;
        }
       else if (
                        fileExtensionOrItems.equalsIgnoreCase("mp3") ||
                        fileExtensionOrItems.equalsIgnoreCase("aac") ||
                        fileExtensionOrItems.equalsIgnoreCase("m4a") ||
                        fileExtensionOrItems.equalsIgnoreCase("m4b") ||
                        fileExtensionOrItems.equalsIgnoreCase("amr") ||
                        fileExtensionOrItems.equalsIgnoreCase("awb") ||
                        fileExtensionOrItems.equalsIgnoreCase("mid") ||
                        fileExtensionOrItems.equalsIgnoreCase("xmf") ||
                        fileExtensionOrItems.equalsIgnoreCase("mxmf") ||
                        fileExtensionOrItems.equalsIgnoreCase("rtttl") ||
                        fileExtensionOrItems.equalsIgnoreCase("rtx") ||
                        fileExtensionOrItems.equalsIgnoreCase("ota") ||
                        fileExtensionOrItems.equalsIgnoreCase("imy") ||
                        fileExtensionOrItems.equalsIgnoreCase("ogg") ||
                        fileExtensionOrItems.equalsIgnoreCase("oga") ||
                        fileExtensionOrItems.equalsIgnoreCase("wav") ||
                        fileExtensionOrItems.equalsIgnoreCase("wave") ||
                        fileExtensionOrItems.equalsIgnoreCase("opus")) {

            return mIcons.AUDIO_ICON;
        }
       else if (
                        fileExtensionOrItems.equalsIgnoreCase("jpeg") ||
                        fileExtensionOrItems.equalsIgnoreCase("jpg") ||
                        fileExtensionOrItems.equalsIgnoreCase("gif") ||
                        fileExtensionOrItems.equalsIgnoreCase("png") ||
                        fileExtensionOrItems.equalsIgnoreCase("bmp") ||
                        fileExtensionOrItems.equalsIgnoreCase("webp")) {
            return mIcons.IMAGE_ICON;

        }
       else if (
                        fileExtensionOrItems.equalsIgnoreCase("rar") ||
                        fileExtensionOrItems.equalsIgnoreCase("zip") ||
                        fileExtensionOrItems.equalsIgnoreCase("tar") ||
                        fileExtensionOrItems.equalsIgnoreCase("iso") ||
                        fileExtensionOrItems.equalsIgnoreCase("gz") ||
                        fileExtensionOrItems.equalsIgnoreCase("bz2") ||
                        fileExtensionOrItems.equalsIgnoreCase("rz") ||
                        fileExtensionOrItems.equalsIgnoreCase("z")) {

            return mIcons.ARCHIVE_ZIP;
        }
        else if (fileExtensionOrItems.equalsIgnoreCase("txt")) {
            return mIcons.TEXT_ICON;
        }
       else if (
                        fileExtensionOrItems.equalsIgnoreCase("css") ||
                        fileExtensionOrItems.equalsIgnoreCase("htm") ||
                        fileExtensionOrItems.equalsIgnoreCase("html") ||
                        fileExtensionOrItems.equalsIgnoreCase("js") ||
                        fileExtensionOrItems.equalsIgnoreCase("aspx")) {
            return mIcons.SCRIPT_ICON;

        }
        else if (fileExtensionOrItems.equalsIgnoreCase("torrent")) {
            return mIcons.TORRENT_ICON;
        }else if(fileExtensionOrItems.equalsIgnoreCase("pgp")){
            return mIcons.ENCRYPT_ICON;
        }else if(fileExtensionOrItems.equalsIgnoreCase("pdf")){
            return mIcons.PDF;
        }
        else {
            return mIcons.BLANK_ICON;
        }
    }
}
