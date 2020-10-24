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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkManager
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.trinity.weatherapp.api.RetrofitApi
import com.trinity.weatherapp.api.RetrofitApiBuilder
import com.trinity.weatherapp.model.UserLocation
import com.trinity.weatherapp.repository.WeatherRepository
import com.trinity.weatherapp.viewmodel.WeatherViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: WeatherViewModel

    @Inject
    lateinit var repository: WeatherRepository

    private val UPDATE_INTERVAL = 10 * 1000 /* 10 secs */.toLong()
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    var detectLocation : MutableLiveData<UserLocation> = MutableLiveData();

    private var mLocationRequest: LocationRequest? = null

    lateinit var workManager: WorkManager
    private val REQUEST_LOCATION = 1
    lateinit var locationManager: LocationManager
    lateinit var latitude: String
    lateinit var longitude: String
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

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest, mLocationCallback/*object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // do work here
                    onLocationChanged(locationResult.lastLocation)
                }
            }*/,
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
        // New location has now been determined
        /*val msg = "Updated Location: " +
                java.lang.Double.toString(location.getLatitude()) + "," +
                java.lang.Double.toString(location.getLongitude())
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()*/
        // You can now create a LatLng Object for use with maps
        val latLng = LatLng(location.getLatitude(), location.getLongitude())
        viewModel.latLng.postValue(latLng)
        Log.d("MainACtivity", "lat:"+latLng.latitude+" - lat:"+latLng.longitude)
        getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);

    }

    fun getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        val locationClient: FusedLocationProviderClient = getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationClient.lastLocation
            .addOnSuccessListener { location -> // GPS location can be null if GPS is switched off
                location?.let { onLocationChanged(it) }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
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
   /* public fun startLocationProcess() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS()
        } else {
            getLocation()
        }
    }*/


    /*private fun OnGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Enable GPS").setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {
            val locationGPS: Location =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

            if (locationGPS != null) {
                val lat = locationGPS.latitude
                val longi = locationGPS.longitude
                latitude = lat.toString()
                longitude = longi.toString()
                viewModel.latitute.value = lat
                viewModel.longitude.value = longi
                viewModel.initializeWorker(workManager)
            } else {

            }
        }
    }*/
}