package com.abhi.questaway.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.abhi.questaway.R
import com.abhi.questaway.events.SelectImageEvent
import com.abhi.questaway.events.TextViewSetEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeFragment : Fragment() {

    lateinit var button: Button
    lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        button = fragmentView.findViewById(R.id.upload_button)
        textView = fragmentView.findViewById(R.id.textView)
        button.setOnClickListener {
            EventBus.getDefault().post(SelectImageEvent())
        }
        return fragmentView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setText(event: TextViewSetEvent) {
        textView.text = event.linewiseText
    }
}

