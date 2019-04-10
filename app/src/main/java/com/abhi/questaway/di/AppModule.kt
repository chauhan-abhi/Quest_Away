package com.abhi.questaway.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(val app: Application) {

    @Provides
    @Singleton
    fun providesApplicationContext(): Application = app

    @Provides
    @Singleton
    fun providesContext(app: Application): Context = app.applicationContext
}