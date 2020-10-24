package com.trinity.weatherapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trinity.weatherapp.MainActivity
import com.trinity.weatherapp.R
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.model.WeatherMapResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface WeatherRepository {
    fun currentWeather(service: RetrofitApi, zip: String, apiKey: String) : MutableLiveData<WeatherMapResponse>
}
class WeatherRepositoryImpl @Inject constructor() : WeatherRepository {
    private val weatherMapResponseLiveData: MutableLiveData<WeatherMapResponse> =
        MutableLiveData()

    override fun currentWeather(service: RetrofitApi, zip: String, apiKey: String): MutableLiveData<WeatherMapResponse> {
        val call = service.getCurrentWeather(zip, apiKey)

        call.enqueue(object : Callback<WeatherMapResponse> {
            override fun onResponse(call: Call<WeatherMapResponse>, response: Response<WeatherMapResponse>) {
                if (response.isSuccessful){
                    return weatherMapResponseLiveData.postValue(response.body())
                }
            }
            override fun onFailure(call: Call<WeatherMapResponse>, t: Throwable) {
            }
        })
        return weatherMapResponseLiveData
    }

}