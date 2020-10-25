package com.trinity.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.maps.model.LatLng
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.database.DataBaseHandler
import com.trinity.weatherapp.jobscheduler.HourlyWorker
import com.trinity.weatherapp.model.WeatherMapResponse
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.util.TemperatureConverter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class WeatherViewModel @Inject constructor() : ViewModel() {

    var latLng: MutableLiveData<LatLng> = MutableLiveData()
    var tempAddress : MutableLiveData<String> = MutableLiveData()
    var zipcode : MutableLiveData<String> = MutableLiveData()
    var databaseData : MutableLiveData<DataBaseHandler.TempResponse?> = MutableLiveData()
    val initialLizeWorker : MutableLiveData<Boolean> = MutableLiveData()
    private val weatherMapResponseLiveData: MutableLiveData<WeatherMapResponse> =
        MutableLiveData()

    val isLoading = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
    }

    @SuppressLint("CheckResult")
    fun getCurrentWeather(service: RetrofitApi, ctx: Context, repository: WeatherRepository, lat: Double, longi: Double, apiKey: String)
            : LiveData<WeatherMapResponse?> {

            repository.currentWeather(service, getZipCode(ctx,lat, longi), apiKey).observe(ctx as LifecycleOwner, Observer {
                weatherMapResponseLiveData.value = it
            })

        weatherMapResponseLiveData.value?.main.let {
            weatherMapResponseLiveData.value?.main?.feelsLike = it?.feelsLike?.let { it1 -> TemperatureConverter().fahrenheit(it1) }
            weatherMapResponseLiveData.value?.main?.tempMax = it?.tempMax?.let { it1 -> TemperatureConverter().fahrenheit(it1) }
            weatherMapResponseLiveData.value?.main?.tempMin = it?.tempMin?.let { it1 -> TemperatureConverter().fahrenheit(it1) }
        }


                return weatherMapResponseLiveData
    }

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    fun initializeWorker(workManager: WorkManager): MutableLiveData<Boolean> {
        workManager.enqueueUniquePeriodicWork(
            "workName",
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequest
                .Builder(HourlyWorker::class.java, 30L, TimeUnit.SECONDS)
                .build())
        return initialLizeWorker
    }

    fun getZipCode(ctx: Context, myLat: Double, myLong: Double) : String{
        val geocoder = Geocoder(ctx, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(myLat, myLong, 1)
        val address: String = addresses[0].getAddressLine(0)

        tempAddress.postValue(address)
        val result = address.filter { it.isDigit() }
        zipcode.postValue(result)

        return result
    }

    fun addToDataBase(mydb: DataBaseHandler, weatherMapResponse: WeatherMapResponse, zipcode: String, tempAddress: String) {
        mydb.insertData(
            response = weatherMapResponse,
            longitude = latLng.value?.longitude.toString(),
            latitude = latLng.value?.latitude.toString(),
            zipcode = zipcode,
            address = tempAddress
        )

        initialLizeWorker.postValue(true)
    }

    fun readFromDataBase(mydb: DataBaseHandler): MutableLiveData<DataBaseHandler.TempResponse?> {
        val screenData = mydb.readData()
        var data : DataBaseHandler.TempResponse? = null

        if (screenData.isNotEmpty()) {
            data = screenData.get(0)
            databaseData.postValue(data)
        } else {
            databaseData.postValue(null)
        }

        return databaseData
    }


}