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

package com.slownet5.pgprootexplorer;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.slownet5.pgprootexplorer.utils.ConfigManager;

/**
 * Created by tripleheader on 1/26/17.
 * Main application class
 *
 */

public class CryptoFM extends Application {

    private static CryptoFM instance;

    public CryptoFM() {
        instance = this;
    }

    public static Context getContext() {
        if(instance==null){
            instance=new  CryptoFM();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        ConfigManager.loadConfig();
    }
}
