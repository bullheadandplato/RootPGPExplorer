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

package com.slownet5.pgprootexplorer.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.slownet5.pgprootexplorer.R;

public class ShowTeamActivity extends AppCompatActivity {
 private String mfirstDevelporName="Usama Bin Omar";
    private String msecondDevelporsName="Asad Yasin";
    private String mworkFirstDevelpor="Application backend architect";
    private String mworkSecondDevelpor="Application design engineer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team);
        TextView fdtextview=(TextView)findViewById(R.id.firstdevelpor_textview);
        TextView sdtextview=(TextView)findViewById(R.id.seconddevelpor_textview);
        TextView fdworktextview=(TextView)findViewById(R.id.work_first_develpor_textview);
        TextView sdworktextview=(TextView)findViewById(R.id.work_second_develpor_textview);
        fdtextview.setText(mfirstDevelporName);
        fdworktextview.setText(mworkFirstDevelpor);
        sdtextview.setText(msecondDevelporsName);
        sdworktextview.setText(mworkSecondDevelpor);
    }
}
