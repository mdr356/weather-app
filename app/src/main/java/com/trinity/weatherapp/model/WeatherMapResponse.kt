package com.trinity.weatherapp.model

data class WeatherMapResponse(
    var coord: Coord? = null,
    var weather: List<Weather>? = null,
    var base: String? = null,
    var main: Main? = null,
    var visibility: Int? = null,
    var wind: Wind? = null,
    var clouds: Clouds? = null,
    var dt: Int? = null,
    var sys: Sys? = null,
    var timezone: Int? = null,
    var id: Int? = null,
    var name: String? = null,
    var cod: Int? = null
)


data class Coord(
    var lon: Double? = null,
    var lat: Double? = null
)

class Clouds(
    var all: Int? = null
)

data class Main (
    var temp: Double? = null,
    var feelsLike: Double? = null,
    var tempMin: Double? = null,
    var tempMax: Double? = null,
    var pressure: Int? = null,
    var humidity: Int? = null
)

data class Sys (
    var type: Int? = null,
    var id: Int? = null,
    var message: Double? = null,
    var country: String? = null,
    var sunrise: Int? = null,
    var sunset: Int? = null
)

class Weather(
    var id: Int? = null,
    var main: String? = null,
    var description: String? = null,
    var icon: String? = null
)

data class Wind(
    var speed: Double? = null,
    var deg: Int? = null
)