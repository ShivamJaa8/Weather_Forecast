package com.example.weatherforecast.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.Hourly_Response.HourlyList
import com.example.weatherforecast.Hourly_Response.WeatherHourlyResponse
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ItemDailyForecastBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HourlyAdapter: RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    private val data=mutableListOf<HourlyList>()
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    private lateinit var date: Date

    companion object {
        // Base URL for OpenWeatherMap icons (adjust if your API is different)
        internal const val WEATHER_ICON_BASE_URL = "https://openweathermap.org/img/wn/"
        internal const val WEATHER_ICON_SUFFIX = "@2x.png" // For high-resolution icons
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding= ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        if(data.size!=0) {
            return 25
        }else{
            return 0
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun SetAdapterResponse(newData: WeatherHourlyResponse){

        data.clear()
        val list = newData.list
        for (forecast in list) {
            val date = inputFormat.parse(forecast.dt_txt)
            val current_date_time = Calendar.getInstance().time
            if (date != null && date >= current_date_time) {

                data.add(forecast)
            }
        }
        notifyDataSetChanged()
    }

    private fun ChangeIcon(condition: String,img: ImageView):Int{

//        when(condition){
//
//            "clouds","few clouds","broken clouds","mist","partly clouds","scattered clouds","overcast clouds","foggy"->{
//                img.setImageResource(R.drawable.cloud_black)
//            }
//            "clear sky","sunny","clear","sky is clear"->{
//                img.setImageResource(R.drawable.sunny)
//            }
//            "light rain","drizzle","moderate rain","rain","showers","heavy intensity rain","heavy rain"->{
//                img.setImageResource(R.drawable.rain)
//            }
//            "snow","light Snow","moderate snow","heavy snow","blizzard"->{
//                img.setImageResource(R.drawable.snow)
//            }
//            else->{
//                img.setImageResource(R.drawable.sunny)
//            }
//        }

        return when (condition) {
            "01d" -> R.drawable.sun
            "01n" -> R.drawable.full_moon
            "02d" -> R.drawable.cloud
            "02n" -> R.drawable.cloud
            "03d", "03n" -> R.drawable.clouds
            "04d", "04n" -> R.drawable.clouds
            "09d", "09n" -> R.drawable.heavy_rain
            "10d", "10n" -> R.drawable.rainy_day
            "11d", "11n" -> R.drawable.storm
            "13d", "13n" -> R.drawable.snow_black
            "50d", "50n" -> R.drawable.mist
            else -> R.drawable.sunny
        }
    }
    inner class ViewHolder(
        private val binding: ItemDailyForecastBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(data: HourlyList){
            with(binding) {

                val date = inputFormat.parse(data.dt_txt)

                textViewHourlyTemp.text = String.format("%s\u00B0C", data.main.temp)

                val condition = data.weather.get(0).icon

                val iconResource=ChangeIcon(condition,imageViewHourlyWeatherIcon)
                imageViewHourlyWeatherIcon.setImageResource(iconResource)

                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = outputFormat.format(date)
                textViewHourlyTime.text = formattedTime

                }
        }

    }
}