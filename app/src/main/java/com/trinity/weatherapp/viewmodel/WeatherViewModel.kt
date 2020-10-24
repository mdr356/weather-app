package com.trinity.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.maps.model.LatLng
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.jobscheduler.HourlyWorker
import com.trinity.weatherapp.model.WeatherMapResponse
import com.trinity.weatherapp.repository.WeatherRepository
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class WeatherViewModel @Inject constructor() : ViewModel() {

    var latLng: MutableLiveData<LatLng> = MutableLiveData<LatLng>()

    private val weatherMapResponseLiveData: MutableLiveData<WeatherMapResponse> =
        MutableLiveData()

    val isLoading = MutableLiveData<Boolean>()

    init {
        isLoading.value = true
    }

    override fun onCleared() {
        super.onCleared()
    }

    @SuppressLint("CheckResult")
    fun getCurrentWeather(service: RetrofitApi, ctx: Context, repository: WeatherRepository, lat: Double, longi: Double, apiKey: String)
            : LiveData<WeatherMapResponse?> {


            repository.currentWeather(service, getZipCode(ctx,lat, longi), apiKey).observe(ctx as LifecycleOwner, Observer {
                weatherMapResponseLiveData.value = it
            })

                return weatherMapResponseLiveData
    }

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    fun initializeWorker(workManager: WorkManager) {
        workManager.enqueueUniquePeriodicWork(
            "workName",
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequest
                .Builder(HourlyWorker::class.java, 30L, TimeUnit.SECONDS)
                .build())
    }

    fun getZipCode(ctx: Context, myLat: Double, myLong: Double) : String{
        val geocoder = Geocoder(ctx, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(myLat, myLong, 1)
        val address: String = addresses[0].getAddressLine(0)

        val result = address.filter { it.isDigit() }

        return result
    }

}