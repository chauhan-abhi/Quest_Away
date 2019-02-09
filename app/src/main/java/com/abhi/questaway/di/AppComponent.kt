package com.abhi.questaway.di

import com.abhi.questaway.QuestAwayApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(app: QuestAwayApplication)
    @Component.Builder
    interface Builder {
        fun appModule(appModule: AppModule):Builder

        fun build(): AppComponent
    }

}