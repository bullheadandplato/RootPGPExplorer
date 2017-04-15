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

package com.osama.cryptofmroot.filemanager.listview;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.osama.cryptofmroot.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/18/16.
 * fill the map of files against path
 */

public  class FileFillerWrapper {
    private static final String TAG = "FileFillerWrapper";
    private  List<DataModelFiles> allFiles   = new ArrayList<>();
    private  int totalFilesCount             = 0;
    private FileListAdapter mAdapter;

    private  String currentPath;


    public  void fillData(String current,FileListAdapter mAdapter){
        this.mAdapter=mAdapter;
       new FileFillerTask().execute(current);
    }
    public  DataModelFiles getFileAtPosition(int position){
        return allFiles.get(position);
    }

    public  int getTotalFilesCount() {
        return totalFilesCount;
    }

    public  String getCurrentPath() {
        return currentPath;
    }

    private void sortData(){
        DataModelFiles md;
        int size=allFiles.size();
        for (int i = 0; i < size ; i++) {
            md=allFiles.get(i);
            if(!md.getFile()){
                allFiles.remove(md);
                allFiles.add(0,md);
            }
        }
    }
    private class FileFillerTask extends AsyncTask<String,Integer,Void>{

        @Override
        protected Void doInBackground(String... path) {
            Log.d(TAG, "fillData: Current path is : "+path[0]);
            currentPath=path[0];
            totalFilesCount=0;
            //for each file in current path fill data
            File file              = FileUtils.getFile(currentPath);
            if(FileUtils.checkReadStatus(file)){
                if(file.list().length>0){
                    allFiles.clear();
                    for (File f: file.listFiles()) {
                        //only add file which I can read
                         if(FileUtils.checkReadStatus(f)){
                             allFiles.add(new DataModelFiles(f));
                             totalFilesCount++;
                    }
                }
                sortData();
            }
        }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: changing google");
            mAdapter.notifyDataSetChanged();
        }
    }
}
