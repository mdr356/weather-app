package com.trinity.weatherapp.api

import com.trinity.weatherapp.model.WeatherMapResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitApi {

    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("zip") zip: String,
        @Query("appid") appid: String
    ): Call<WeatherMapResponse>

}