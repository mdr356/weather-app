package com.trinity.weatherapp.jobscheduler

import android.content.Context
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.trinity.weatherapp.R
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.api.RetrofitApiBuilder
import com.trinity.weatherapp.database.DataBaseHandler
import com.trinity.weatherapp.model.WeatherMapResponse
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.util.Connectivity
import com.trinity.weatherapp.viewmodel.WeatherViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class HourlyWorker(val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    lateinit var service: RetrofitApi

    var mydb: DataBaseHandler = DataBaseHandler(ctx)
    val isConnectedWifi = Connectivity().isConnectedWifi(ctx)
    override fun doWork(): Result {
        service = RetrofitApiBuilder.buildService(RetrofitApi::class.java)

        if (isConnectedWifi) {
            val weatherMapResponse: MutableList<DataBaseHandler.TempResponse> = mydb.readData()

            weatherMapResponse.get(0).latitude?.let { lat ->
                weatherMapResponse.get(0).longitude?.let { longi ->

                    val call = service.getCurrentWeather( weatherMapResponse.get(0).zipcode!!, ctx.getString(R.string.api_key))

                    call.enqueue(object : Callback<WeatherMapResponse> {
                        override fun onResponse(call: Call<WeatherMapResponse>, response: Response<WeatherMapResponse>) {
                            if (response.isSuccessful){
                                mydb.insertData(response.body()!!, lat.toString(), longi.toString(),weatherMapResponse.get(0).zipcode!!, weatherMapResponse.get(0).address!!)
                            }
                        }
                        override fun onFailure(call: Call<WeatherMapResponse>, t: Throwable) {
                        }
                    })
                }
            }
        }
        return Result.success()
    }
}
