package com.abhi.questaway.di

import com.abhi.questaway.QuestAwayApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, BuildersModule::class, AppModule::class, NetModule::class])
interface AppComponent {
    fun inject(app: QuestAwayApplication)
}