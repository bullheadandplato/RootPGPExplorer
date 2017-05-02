package com.osama.cryptofmroot.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.osama.cryptofmroot.R;

public class ShowTeamActivity extends AppCompatActivity {
 private String firstDevelporName="Android Developers\nUsama Bin Omar";
    private String secondDevelporsName="Asad Yasin";
    private String workFirstDevelpor="Application backend architect";
    private String workSecondDevelpor="Application design engineer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team);
        TextView fdtextview=(TextView)findViewById(R.id.firstdevelpor_textview);
        TextView sdtextview=(TextView)findViewById(R.id.seconddevelpor_textview);
        TextView fdworktextview=(TextView)findViewById(R.id.work_first_develpor_textview);
        TextView sdworktextview=(TextView)findViewById(R.id.work_second_develpor_textview);
        fdtextview.setText(firstDevelporName);
        fdworktextview.setText(workFirstDevelpor);
        sdtextview.setText(secondDevelporsName);
        sdworktextview.setText(workSecondDevelpor);
    }
}
