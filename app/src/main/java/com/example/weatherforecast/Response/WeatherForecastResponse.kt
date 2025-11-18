package com.example.weatherforecast.Response


import java.io.Serializable

data class WeatherForecastResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Double
)

object WeatherDataHolder {
    var response: WeatherForecastResponse? = null
}