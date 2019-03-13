package com.abhi.questaway

import android.app.Application
import com.abhi.questaway.di.Injector

class QuestAwayApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Injector.init(this)
    }

}