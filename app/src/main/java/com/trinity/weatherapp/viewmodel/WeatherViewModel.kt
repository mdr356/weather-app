package com.trinity.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trinity.weatherapp.model.WeatherMapResponse
import com.trinity.weatherapp.repository.WeatherRepository
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject


class WeatherViewModel @Inject constructor() : ViewModel() {

    private val weatherMapResponseLiveData: MutableLiveData<WeatherMapResponse> =
        MutableLiveData()


    val isLoading = MutableLiveData<Boolean>()

    init {
        isLoading.value = true
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    private lateinit var disposable : Disposable

    fun getCurrentWeather(repository: WeatherRepository,cityName: String, apiKey: String) : LiveData<WeatherMapResponse?> {
         disposable = repository.currentWeather(cityName,apiKey)
            .subscribeWith(
                object : DisposableSingleObserver<WeatherMapResponse?>() {
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onSuccess(response: WeatherMapResponse?) {
                        weatherMapResponseLiveData.value = response
                    }
                }
            )

        return weatherMapResponseLiveData;
    }
}