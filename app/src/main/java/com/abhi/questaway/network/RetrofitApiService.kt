package com.abhi.questaway.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApiService {

    @GET("submit/")
    fun getResult(
        @Query("paragraph") paragraph: String,
        @Query("question") question: String
    ): Call<ResultModel>

}