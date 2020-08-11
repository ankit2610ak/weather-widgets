package com.example.talkcharge.retrofit

import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    companion object {
    }

    @POST("forecast")
    fun getWeatherForecast(
        @Query("lat") lat: Float,
        @Query("lon") name: Float,
        @Query("appid") appid: String

    )

}