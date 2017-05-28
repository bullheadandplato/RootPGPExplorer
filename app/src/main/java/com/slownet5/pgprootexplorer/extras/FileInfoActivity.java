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

package com.slownet5.pgprootexplorer.extras;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.filemanager.listview.DataModelFiles;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.root.RootUtils;
import com.slownet5.pgprootexplorer.utils.CommonConstants;
import com.slownet5.pgprootexplorer.utils.FileUtils;
import com.slownet5.pgprootexplorer.utils.MainUtils;

public class FileInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_info);
        MainUtils.closeActionBarButton(this);
        init();
    }
    private void init(){
        DataModelFiles files= SharedData.CURRENT_FILE_FOR_INFO;
        ((ImageView)findViewById(R.id.fileinfo_image)).setImageDrawable(files.getFileIcon());
        ((TextView)findViewById(R.id.fileinfo_name_textview)).setText(files.getFileName());
        String fileSize;
        String completeFilename=files.getFilePath()+files.getFileName();
        if(RootUtils.isRootPath(completeFilename)){
            fileSize=RootUtils.getFileSize(completeFilename);
        }else {
            Log.d("looo", "init: complete filename is: "+completeFilename);
            fileSize=files.getFile() ? files.getFileDate() : FileUtils.getFolderSize(completeFilename);
        }
        ((TextView)findViewById(R.id.fileinfo_size_textview)).setText(fileSize);
        String tmp=files.isEncrypted() ? "Encrypted" : "Not encrypted";
        ((TextView)findViewById(R.id.fileinfo_permission_textview)).setText(tmp);
    }
}
