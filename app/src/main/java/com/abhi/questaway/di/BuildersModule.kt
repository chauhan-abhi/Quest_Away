package com.abhi.questaway.di

import com.abhi.questaway.view.ParagraphActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeParagraphActivity(): ParagraphActivity
}