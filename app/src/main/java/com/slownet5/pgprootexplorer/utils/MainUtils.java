package com.slownet5.pgprootexplorer.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.slownet5.pgprootexplorer.R;
/**
 *
 * Created by bullhead on 5/24/17.
 *
 */

public class MainUtils {
    public static void closeActionBarButton(AppCompatActivity act){
        assert act.getSupportActionBar()!=null;
        act.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        act.getSupportActionBar().setHomeButtonEnabled(true);

    }
}
