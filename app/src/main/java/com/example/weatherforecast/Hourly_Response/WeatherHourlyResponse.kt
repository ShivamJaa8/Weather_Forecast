package com.example.weatherforecast.Hourly_Response

data class WeatherHourlyResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<HourlyList>,
    val message: Int
)