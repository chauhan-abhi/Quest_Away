package com.abhi.questaway.di

import android.app.Application
import android.content.Context
import com.abhi.questaway.network.RetrofitApiService
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class AppModule(internal var application: Application) {

    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Retrofit) = retrofit.create(RetrofitApiService::class.java)

    @Provides
    @Singleton
    internal fun provideRetrofitInterface(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://35.229.109.6:1995/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Provides
    @Singleton
    internal fun providesApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    internal fun providesContext(application: Application): Context {
        return application.applicationContext
    }
}