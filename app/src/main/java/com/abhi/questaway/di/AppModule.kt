package com.abhi.questaway.di

import android.app.Application
import android.content.Context
import com.abhi.questaway.network.RetrofitApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
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