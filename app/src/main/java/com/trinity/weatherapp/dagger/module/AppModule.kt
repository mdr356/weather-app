package com.trinity.weatherapp.dagger.module

import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.api.RetrofitApiBuilder
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.repository.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


private const val BASE_URL = "http://api.openweathermap.org/"

/*@Module
class ApiClientModule {
    @Singleton
    @Provides
    fun provideRetrofitApi() = RetrofitApiBuilder.create(RetrofitApi::class.java)

}*/


@Module
class AppModule {

    @Provides
    fun provideWeatherRepository() : WeatherRepository = WeatherRepositoryImpl()

}