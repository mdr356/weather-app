package com.trinity.weatherapp.util

class TemperatureConverter {

    companion object {
        val CONSTANT_1_8  = 1.8
        val CONSTANT_273 = 273
        val CONSTANT_32 = 32
    }
    fun fahrenheit(kelvin: Double): Double {
        return ((CONSTANT_1_8*(kelvin - CONSTANT_273) + CONSTANT_32))
    }
}