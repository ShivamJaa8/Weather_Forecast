package com.example.weatherforecast


import com.example.weatherforecast.Hourly_Response.HourlyList
import com.example.weatherforecast.Hourly_Response.WeatherHourlyResponse
import com.example.weatherforecast.Location.GetLocationResponseItem
import com.example.weatherforecast.Response.WeatherForecastResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OpenWeatherApiService {

// Forecast For Upto 16 Days by City Name
//    @GET("data/2.5/forecast/daily")
//    fun getDailyForecast(
//        @Query("q") city: String,
//        @Query("cnt") count: Int, // Number of days (e.g., 7)
//        @Query("appId") apiKey: String,
//        @Query("units") units: String = "metric"
//    ): Call<WeatherForecastResponse>


//Forecast for Upto 16 Days by Latitude and Longitude
     @GET("data/2.5/forecast/daily")
        fun getDailyForecast(
            @Query("lat")latitude:String,
            @Query("lon")longitude:String,
            @Query("cnt") count: Int, // Number of days (e.g., 7)
            @Query("appId") apiKey: String,
            @Query("units") units: String = "metric"
        ): Call<WeatherForecastResponse>

    //Forecast for Hourly by Latitude and Longitude
//    @GET("data/2.5/forecast/hourly")
//    fun GetHourlyForecast(
//        @Query("q")city:String,
//        @Query("appId")apiKey:String,
//        @Query("units")units: String = "metric"
//    ): Call<WeatherHourlyResponse>

    //Forecast for Hourly by Latitude and Longitude
    @GET("data/2.5/forecast/hourly")
    fun GetHourlyForecast(
        @Query("lat")latitude:String,
        @Query("lon")longitude:String,
        @Query("appId")apiKey:String,
        @Query("units")units: String = "metric"
    ): Call<WeatherHourlyResponse>

    @GET("search")
    fun GetLocationResult(
        @Query("q") name:String,
        @Query("format") format:String="json",
    ):Call<List<GetLocationResponseItem>>
//    https://nominatim.openstreetmap.org/search?q={your_query}&format=json

}