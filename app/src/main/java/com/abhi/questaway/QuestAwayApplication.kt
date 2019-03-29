package com.abhi.questaway

import android.app.Activity
import android.app.Application
import com.abhi.questaway.di.Injector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class QuestAwayApplication: Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        Injector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

}