package com.osama.cryptofmroot.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.osama.cryptofmroot.R;

public class ShowTeamActivity extends AppCompatActivity {
 private String mfirstDevelporName="Usama Bin Omar";
    private String msecondDevelporsName="Asad Yasin";
    private String mworkFirstDevelpor="Application backend architect";
    private String mworkSecondDevelpor="Application design engineer";
    private String mdvelopers="Develpor's";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team);
        TextView develportextview=(TextView)findViewById(R.id.develpor_textview);
        TextView fdtextview=(TextView)findViewById(R.id.firstdevelpor_textview);
        TextView sdtextview=(TextView)findViewById(R.id.seconddevelpor_textview);
        TextView fdworktextview=(TextView)findViewById(R.id.work_first_develpor_textview);
        TextView sdworktextview=(TextView)findViewById(R.id.work_second_develpor_textview);
        develportextview.setText(mdvelopers);
        fdtextview.setText(mfirstDevelporName);
        fdworktextview.setText(mworkFirstDevelpor);
        sdtextview.setText(msecondDevelporsName);
        sdworktextview.setText(mworkSecondDevelpor);
    }
}
