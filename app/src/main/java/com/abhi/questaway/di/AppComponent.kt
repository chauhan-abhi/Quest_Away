package com.abhi.questaway.di

import com.abhi.questaway.QuestAwayApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(app: QuestAwayApplication)

}