package com.trinity.weatherapp.jobscheduler

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.trinity.weatherapp.R
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.database.DataBaseHandler
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.util.Connectivity
import com.trinity.weatherapp.viewmodel.WeatherViewModel
import javax.inject.Inject


class HourlyWorker(val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    @Inject
    lateinit var repository: WeatherRepository

    @Inject
    lateinit var viewModel: WeatherViewModel

    @Inject
    lateinit var service: RetrofitApi

    var mydb: DataBaseHandler = DataBaseHandler(ctx)
    val isConnectedWifi = Connectivity().isConnectedWifi(ctx)
    override fun doWork(): Result {

        if (isConnectedWifi) {
            val weatherMapResponse: MutableList<DataBaseHandler.TempResponse> = mydb.readData()

            weatherMapResponse.get(0).latitude?.let { lat ->
                weatherMapResponse.get(0).longitude?.let { longi ->
                    viewModel.getCurrentWeather(
                        service,
                        ctx, repository, lat, longi,
                        this.ctx.getString(R.string.api_key)
                    ).observe(ctx as LifecycleOwner, Observer {
                        mydb.insertData(it!!, lat.toString(), longi.toString())
                    })
                }
            }
        }
        return Result.success()
    }
}
