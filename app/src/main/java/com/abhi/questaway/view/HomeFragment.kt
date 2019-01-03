package com.abhi.questaway.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.abhi.questaway.R
import com.abhi.questaway.events.SelectImageEvent
import org.greenrobot.eventbus.EventBus

class HomeFragment : Fragment() {

    lateinit var button: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        button = fragmentView.findViewById(R.id.upload_button)
        button.setOnClickListener {
            EventBus.getDefault().post(SelectImageEvent())
        }
        return fragmentView
    }
}
