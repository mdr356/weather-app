package com.trinity.weatherapp.api

import com.trinity.weatherapp.model.WeatherMapResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitApi {

    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") appid: String
    ): Single<WeatherMapResponse>

}