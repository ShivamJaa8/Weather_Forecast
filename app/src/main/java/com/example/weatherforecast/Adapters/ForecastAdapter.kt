package com.example.weatherforecast.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.Activities.DetailedForecast
import com.example.weatherforecast.R
import com.example.weatherforecast.Response.Item0
import com.example.weatherforecast.databinding.ItemForecastBinding
import com.squareup.picasso.Picasso
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale


class ForecastAdapter: RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>(){

    private var forecast = mutableListOf<Item0>()

    companion object {
        private const val WEATHER_ICON_BASE_URL = "https://openweathermap.org/img/wn/"
        private const val WEATHER_ICON_SUFFIX = "@2x.png" // For high-resolution icons
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastViewHolder {

        val binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ForecastViewHolder,
        position: Int
    ) {
        holder.bind(forecast[position])
    }

    override fun getItemCount(): Int {
        return forecast.size
    }

    private fun formatDate(timestampInSeconds: Long): String {
        val date = Date(timestampInSeconds * 1000L)
        val simpleDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    private fun formatDay(timestampInSeconds: Long): String {
        val date = Date(timestampInSeconds * 1000L)
        val simpleDayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        return simpleDayFormat.format(date)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun RefreshLayout(newForecasts: List<Item0>) {
        forecast.clear()
        forecast.addAll(newForecasts)
        notifyDataSetChanged()
    }

    private fun getIconUrl(iconCode: String): String {
        return "$WEATHER_ICON_BASE_URL$iconCode$WEATHER_ICON_SUFFIX"
    }

    inner class ForecastViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item0){
            with(binding) {
                textViewDay.text = formatDay(item.dt.toLong())
                textViewDate.text = formatDate(item.dt.toLong())
                textViewCondition.text = item.weather.get(0).description.uppercase()
                val temp =  String.format("%s\u00B0C - ", item.temp.min.toInt()) + String.format("%s\u00B0C", item.temp.max.toInt())
                textViewTemp.text = temp

//                item.weather.get(0).icon?.let { iconCode ->
//                    val iconUrl = getIconUrl(iconCode)
//                    Picasso.get().load(iconUrl).into(imageViewWeatherIcon)
//                } ?: run {
//                    imageViewWeatherIcon.setImageResource(R.drawable.sunny)
//                }

                itemView.setOnClickListener {
                    val i=Intent(itemView.context, DetailedForecast::class.java)
                    i.putExtra("EXTRA_WEATHER_DATA", item)
                    itemView.context.startActivity(i) }
            }
        }
    }
}
