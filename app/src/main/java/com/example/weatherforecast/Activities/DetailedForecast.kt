package com.example.weatherforecast.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.Response.Item0
import com.example.weatherforecast.databinding.ActivityDetailedForecastBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailedForecast : AppCompatActivity() {

    private val binding: ActivityDetailedForecastBinding by lazy{
        ActivityDetailedForecastBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val weatherData = intent.getParcelableExtra<Item0>("EXTRA_WEATHER_DATA")

        with(binding) {
            toolbar.setNavigationOnClickListener { finish() }

            if (weatherData != null) {

                // Format the day and date from the timestamp
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                val date = Date(weatherData.dt.toLong() * 1000) // Convert seconds to milliseconds

                detailTextViewDay.text = dayFormat.format(date)
                detailTextViewDate.text = dateFormat.format(date)

                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                timeFormat.timeZone = TimeZone.getDefault() // Use the device's default timezone

                val sunriseTime = Date(weatherData.sunrise.toLong() * 1000)
                val sunsetTime = Date(weatherData.sunset.toLong() * 1000)

                detailTextViewSunrise.text = timeFormat.format(sunriseTime)
                detailTextViewSunset.text = timeFormat.format(sunsetTime)

                // Populate other data from the weatherData object
                detailTextViewHumidity.text = "${weatherData.humidity}%"
                val min_temp =  weatherData.temp.min.toString()
                val max_temp = weatherData.temp.max.toString()
                val temp="$min_temp-$max_temp°C"

                detailTextViewCurrentTemp.text = temp
                val condition = weatherData.weather.get(0).description.toString()
                detailTextViewCondition.text=condition.uppercase()
                ChangeIcon(condition)

                detailTextViewCloudiness.text = "${weatherData.clouds}%"
                detailTextViewPressure.text = "${weatherData.pressure} hPa"
                detailTextViewRainPossibility.text = "${weatherData.rain}%"
                detailTextViewWindGust.text = "${"%.1f".format(weatherData.gust)} Km/h"
                detailTextViewWindSpeed.text = "${"%.1f".format(weatherData.speed)} Km/h"

            }
        }

    }

    private fun ChangeIcon(condition: String){

        when(condition){

            "clouds","few clouds","mist","partly clouds","overcast clouds","foggy"->{
                binding.detailImageViewWeatherIcon.setAnimation(R.raw.cloud)
            }
            "clear sky","sunny","clear","sky is clear"->{
                binding.detailImageViewWeatherIcon.setAnimation(R.raw.sun)
            }
            "light rain","drizzle","moderate rain","rain","showers","heavy intensity rain","heavy rain"->{
                binding.detailImageViewWeatherIcon.setAnimation(R.raw.rain)
            }
            "snow","light Snow","moderate snow","heavy snow","blizzard"->{
                binding.detailImageViewWeatherIcon.setAnimation(R.raw.snow)
            }
            else->{
                binding.detailImageViewWeatherIcon.setAnimation(R.raw.sun)
            }
        }
        binding.detailImageViewWeatherIcon.playAnimation()
    }

}