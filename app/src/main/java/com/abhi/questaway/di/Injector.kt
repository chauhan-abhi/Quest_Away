package com.abhi.questaway.di

import com.abhi.questaway.QuestAwayApplication

object Injector {
    lateinit var appComponent: AppComponent

    fun init(app: QuestAwayApplication) {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .netModule(NetModule())
            .build()
        appComponent.inject(app)
    }

}