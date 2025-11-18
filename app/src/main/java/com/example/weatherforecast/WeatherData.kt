package com.example.weatherforecast

import com.example.weatherforecast.Hourly_Response.HourlyList
import com.example.weatherforecast.Response.Item0
import com.example.weatherforecast.Response.WeatherForecastResponse


sealed class WeatherData


data class HourlyWeatherData(
    val data: HourlyList
): WeatherData()


//data class

