package com.example.weatherforecast.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherforecast.Adapters.ForecastAdapter
import com.example.weatherforecast.R
import com.example.weatherforecast.Response.WeatherDataHolder
import com.example.weatherforecast.Response.WeatherForecastResponse
import com.example.weatherforecast.databinding.ActivityForecastBinding
import java.text.SimpleDateFormat
import java.util.Locale

class Forecast : AppCompatActivity() {

    private val binding : ActivityForecastBinding by lazy {
        ActivityForecastBinding.inflate(layoutInflater)
    }

    private val adapter = ForecastAdapter()
    private lateinit var forecast : WeatherForecastResponse

    private val API_KEY = "2a1a2207b7cbcca26aae41ad311576d1"
    private val BASE_URL = "http://api.openweathermap.org/"


    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        forecast= WeatherDataHolder.response!!
//        val i= intent
//        val forecast= (i.getSerializableExtra("ForecastResponse") as? WeatherForecastResponse)
        if(forecast!=null){
            adapter.RefreshLayout(forecast.list)

        }else{
            Toast.makeText(this@Forecast,"Error to get data", Toast.LENGTH_SHORT).show()
        }


        binding.dailyForecastRecyclerView.adapter=adapter
//        CallForecastApi()

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    private fun CallForecastApi() {


    }
}