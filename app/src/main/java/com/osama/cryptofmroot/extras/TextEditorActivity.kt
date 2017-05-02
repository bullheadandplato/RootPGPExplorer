package com.osama.cryptofmroot.extras

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.osama.cryptofmroot.R
import kotlinx.android.synthetic.main.activity_text_editor.*

class TextEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        kotlin_textview.setText("Osama Bin Omar is now using kotlin");
    }
}
