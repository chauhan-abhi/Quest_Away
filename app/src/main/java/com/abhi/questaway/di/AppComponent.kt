package com.abhi.questaway.di

import com.abhi.questaway.QuestAwayApplication
import com.abhi.questaway.view.ParagraphActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(app: QuestAwayApplication)
    fun inject(paragraphActivity: ParagraphActivity)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        fun appModule(appModule: AppModule):Builder
    }

}