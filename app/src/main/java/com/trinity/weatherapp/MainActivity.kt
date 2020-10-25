package com.trinity.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkManager
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.api.RetrofitApiBuilder
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.viewmodel.WeatherViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: WeatherViewModel

    @Inject
    lateinit var repository: WeatherRepository

    private val UPDATE_INTERVAL = 7200 * 1000 /*  2hours */.toLong()
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    private var mLocationRequest: LocationRequest? = null

    lateinit var workManager: WorkManager
    private val REQUEST_LOCATION = 1
    lateinit var locationManager: LocationManager
    lateinit var request: RetrofitApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        //startLocationUpdates()
        locationManager = (getSystemService(LOCATION_SERVICE) as LocationManager?)!!

        workManager = WorkManager.getInstance(this)

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)

        request = RetrofitApiBuilder.buildService(RetrofitApi::class.java)

        val workManager = WorkManager.getInstance(this)
        viewModel.initialLizeWorker.observe(this as LifecycleOwner, Observer {
            viewModel.initializeWorker(workManager)
        })
    }


    // Trigger new location updates at interval
    fun startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest?.setInterval(UPDATE_INTERVAL)
        mLocationRequest?.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                onLocationChanged(locationResult.lastLocation)
            }
        }
    }
    fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.getLatitude(), location.getLongitude())
        viewModel.latLng.postValue(latLng)
        Log.d("MainACtivity", "lat:"+latLng.latitude+" - lat:"+latLng.longitude)
        getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback) // STOP Requesting location here.
    }

    fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION
        )
    }
}