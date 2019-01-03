package com.abhi.questaway

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.abhi.questaway.base.FilePickerActivity

class MainActivity : FilePickerActivity() {

    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun setUpComponent() {
    }

    override fun setUpViewHolder(view: View?) {
    }
}
