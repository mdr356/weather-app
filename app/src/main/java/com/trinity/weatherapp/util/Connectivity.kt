package com.trinity.weatherapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class Connectivity {

    fun isConnectedWifi(context: Context): Boolean {
        getNetworkInfo(context).let {info ->
            return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
        }
    }

    /**
     * Get the network info
     * @param context
     * @return
     */
    fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }
}