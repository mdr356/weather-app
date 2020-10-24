package com.trinity.weatherapp.dagger.module

import androidx.lifecycle.ViewModel
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.api.RetrofitApiBuilder
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.repository.WeatherRepositoryImpl
import com.trinity.weatherapp.viewmodel.WeatherViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideRetrofitApi() = createRetrofitApi()

    fun createRetrofitApi(): RetrofitApi {
        return RetrofitApiBuilder.create(RetrofitApi::class.java)
    }

    @Provides
    fun provideWeatherRepository(service: RetrofitApi) : WeatherRepository = WeatherRepositoryImpl(service)

}