package com.abhi.questaway

import android.app.Application
import com.abhi.questaway.di.AppComponent
import com.abhi.questaway.di.AppModule
import com.abhi.questaway.di.DaggerAppComponent

class QuestAwayApplication: Application() {

    private var mAppComponent: AppComponent? = null

    val appComponent: AppComponent?
        get() {
            if (mAppComponent == null) {
                createAppComponent()
            }
            return mAppComponent
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createAppComponent()
        mAppComponent?.inject(this)

    }

    companion object {
        var instance: QuestAwayApplication? = null
            private set
    }

    private fun createAppComponent() {
        mAppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}