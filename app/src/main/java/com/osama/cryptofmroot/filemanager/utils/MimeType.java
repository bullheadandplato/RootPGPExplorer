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
import android.graphics.drawable.Icon;
import android.icu.util.ICUUncheckedIOException;

/**
 * Created by Shadow on 1/25/2017.
 *
 */

public class MimeType {

    public static Drawable getIcon(String fileExtensionOrItems) {

        if (
                        fileExtensionOrItems.equalsIgnoreCase("mp4") ||
                        fileExtensionOrItems.equalsIgnoreCase("mkv") ||
                        fileExtensionOrItems.equalsIgnoreCase("3gp") ||
                        fileExtensionOrItems.equalsIgnoreCase("webm")) {
            return IconManager.VIDEO_ICON;
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

            return IconManager.AUDIO_ICON;
        }
       else if (
                        fileExtensionOrItems.equalsIgnoreCase("jpeg") ||
                        fileExtensionOrItems.equalsIgnoreCase("jpg") ||
                        fileExtensionOrItems.equalsIgnoreCase("gif") ||
                        fileExtensionOrItems.equalsIgnoreCase("png") ||
                        fileExtensionOrItems.equalsIgnoreCase("bmp") ||
                        fileExtensionOrItems.equalsIgnoreCase("webp")) {
            return IconManager.IMAGE_ICON;

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

            return IconManager.ARCHIVE_ZIP;
        }
        else if (fileExtensionOrItems.equalsIgnoreCase("txt")) {
            return IconManager.TEXT_ICON;
        }
       else if (
                        fileExtensionOrItems.equalsIgnoreCase("css") ||
                        fileExtensionOrItems.equalsIgnoreCase("htm") ||
                        fileExtensionOrItems.equalsIgnoreCase("html") ||
                        fileExtensionOrItems.equalsIgnoreCase("js") ||
                        fileExtensionOrItems.equalsIgnoreCase("aspx") ||
                        fileExtensionOrItems.equalsIgnoreCase("rc")
                ) {
            return IconManager.SCRIPT_ICON;

        }
        else if (fileExtensionOrItems.equalsIgnoreCase("torrent")) {
            return IconManager.TORRENT_ICON;
        }else if(fileExtensionOrItems.equalsIgnoreCase("pgp") ||
                fileExtensionOrItems.equalsIgnoreCase("gpg")
                ){
            return IconManager.ENCRYPT_ICON;
        }else if(fileExtensionOrItems.equalsIgnoreCase("pdf")){
            return IconManager.PDF;
        }else if(
                fileExtensionOrItems.equalsIgnoreCase("docx") ||
                        fileExtensionOrItems.equalsIgnoreCase("doc") ||
                        fileExtensionOrItems.equalsIgnoreCase("odp")
                ){
            return IconManager.DOC;
        } else if (
                fileExtensionOrItems.equalsIgnoreCase("ppt") ||
                fileExtensionOrItems.equalsIgnoreCase("pptx")) {
            return IconManager.PPT;
        } else if(
                fileExtensionOrItems.equalsIgnoreCase("xlsx") ||
                fileExtensionOrItems.equalsIgnoreCase("ods")  ||
                fileExtensionOrItems.equalsIgnoreCase("ots")  ||
                fileExtensionOrItems.equalsIgnoreCase("xls")){
            return IconManager.EXCEL;
        }
        else {
            return IconManager.BLANK_ICON;
        }
    }
}
