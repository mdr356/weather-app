package com.trinity.weatherapp.repository

import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.model.WeatherMapResponse
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

interface WeatherRepository {
    fun currentWeather(cityName: String, apiKey: String) : Single<WeatherMapResponse>
}
class WeatherRepositoryImpl @Inject constructor(var service: RetrofitApi) : WeatherRepository {

    override fun currentWeather(cityName: String, apiKey: String): Single<WeatherMapResponse> {
        return service.getCurrentWeather(cityName = cityName, appid = apiKey)
    }

}