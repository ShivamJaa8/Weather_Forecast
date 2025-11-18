package com.example.weatherforecast.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherforecast.Adapters.HourlyAdapter
import com.example.weatherforecast.Hourly_Response.WeatherHourlyResponse
import com.example.weatherforecast.Location.CurrentLocation
import com.example.weatherforecast.OpenWeatherApiService
import com.example.weatherforecast.R
import com.example.weatherforecast.Response.WeatherDataHolder
import com.example.weatherforecast.Response.WeatherForecastResponse
import com.example.weatherforecast.databinding.ActivityMainBinding
import retrofit2.Call
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import com.example.weatherforecast.isNetworkConnected
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.location.*
//import com.google.android.gms.location.Priority
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val adapter = HourlyAdapter()

    // --- API Constants ---
    private val API_KEY = "2a1a2207b7cbcca26aae41ad311576d1"
    private val BASE_URL = "https://pro.openweathermap.org/"
    private lateinit var ForecastResponse : WeatherForecastResponse


    private val context=this@MainActivity

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest


     private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                checkLocationServices()
            } else {
                Toast.makeText(this, "Precise location permission is required for the best weather forecast.", Toast.LENGTH_LONG).show()
            }
        }

    // This callback will receive the location updates.
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location? = locationResult.lastLocation
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                getCityAndStateNameFromLocation(location.latitude, location.longitude)

                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerView.adapter=adapter
        binding.next7Days.setOnClickListener {
            val i = Intent(this, Forecast::class.java)
//            i.putExtra("ForecastResponse",ForecastResponse)

            startActivity(i )
        }
        binding.searchView.setOnClickListener { startActivity(Intent(this@MainActivity,
            SearchActivity::class.java)) }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .build()

        if(CurrentLocation.location==null){
            checkFineLocationPermission()
        }else{
            CallApiForHourly(CurrentLocation.location!!.lat, CurrentLocation.location!!.lon)

            CallApiForWeekly(CurrentLocation.location!!.lat, CurrentLocation.location!!.lon)

            binding.currentLocation.text= CurrentLocation.location!!.display_name.toString()
        }

        binding.currentLocation.setOnClickListener { checkFineLocationPermission() }

        if(WeatherDataHolder.response==null){
            binding.next7Days.isClickable=false
        }
    }


    private fun Internet():Boolean{

        if (isNetworkConnected(this)) {
            return true
        } else {
            return false
           }

    }

    private fun CallApiForWeekly(lat: String,lon: String) {

        if(!Internet()){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }else {

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherApiService::class.java)

            val response = retrofit.getDailyForecast(lat, lon, 15, API_KEY)
                .enqueue(object : Callback<WeatherForecastResponse> {
                    override fun onResponse(
                        call: Call<WeatherForecastResponse?>,
                        response: Response<WeatherForecastResponse?>
                    ) {
                        val result = response.body()
                        if (result != null && response.isSuccessful) {

                            Toast.makeText(this@MainActivity, "Working", Toast.LENGTH_SHORT).show()

                            WeatherDataHolder.response = result
                                binding.next7Days.isClickable=true

                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                response.errorBody().toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<WeatherForecastResponse?>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@MainActivity,
                            "Unable to Get response\n ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                })
        }

    }

    private fun CallApiForHourly(lat: String,lon: String) {
        if(!Internet()){
//            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }else {

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherApiService::class.java)

            val response = retrofit.GetHourlyForecast(lat, lon, API_KEY, "metric")
                .enqueue(object : Callback<WeatherHourlyResponse> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<WeatherHourlyResponse?>,
                        response: Response<WeatherHourlyResponse?>
                    ) {
                        val result = response.body()
                        if (result != null && response.isSuccessful) {
                            adapter.SetAdapterResponse(result)
//                    Toast.makeText(this@MainActivity, " Working", Toast.LENGTH_SHORT).show()

                            with(binding) {
                                temperature.text =
                                    String.format("%s\u00B0C", result.list.get(0).main.temp)

                                val cond = result.list.get(0).weather.get(0).description
                                condition.text = cond.uppercase()
                                humidity.text = result.list.get(0).main.humidity.toString() + " %"
                                pressure.text = result.list.get(0).main.pressure.toString() + " hPa"
                                windSpeed.text = result.list.get(0).wind.speed.toString() + " hPa"
                                tempFeelsLike.text =
                                    "Feels Like " + result.list.get(0).main.feels_like.toString() + " °C"

                                ChangeIcon(cond)
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<WeatherHourlyResponse?>,
                        t: Throwable
                    ) {
                        Toast.makeText(this@MainActivity, "Not Working", Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }

    private fun ChangeIcon(condition: String){

        when(condition){

            "clouds","mist","partly clouds","overcast clouds","foggy"->{
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "clear sky","sunny","clear"->{
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "light rain","drizzle","moderate rain","rain","showers","heavy intensity rain","heavy rain"->{
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "snow","light Snow","moderate snow","heavy snow","blizzard"->{
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

//    private fun GetCurrentLocation(){
//
//        Toast.makeText(context,"Good", Toast.LENGTH_SHORT).show()
//    }
//    private fun IsLocationPermissionGranted(): Boolean{
//        return ContextCompat.checkSelfPermission(
//            context, Manifest.permission.ACCESS_FINE_LOCATION
//        )== PackageManager.PERMISSION_GRANTED
//    }
//
//
//    private fun requestLocationPermission(){
//        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//    }
//
//
//    private fun proceedWithCurrentLocation(){
//        if(IsLocationPermissionGranted()){
//            GetCurrentLocation()
//        }else{
//            requestLocationPermission()
//        }
//    }

    // Check if the user has granted the ACCESS_FINE_LOCATION permission.
    private fun checkFineLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, now check if location services are enabled.
                checkLocationServices()
            }
            else -> {
                // Request the ACCESS_FINE_LOCATION permission.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Check if the system's location services are enabled.
    private fun checkLocationServices() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Location services are enabled, request a new location.
            requestNewLocation()
        } else {
            // Location services are not enabled. Prompt the user to turn them on.
            Toast.makeText(this, "Please enable location services for this app.", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    // Requests an active location update.
    private fun requestNewLocation() {
        // We already have permission, so we can suppress the lint warning.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }


    // Function to get the city and state name from latitude and longitude using Geocoder.
    private fun getCityAndStateNameFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]

                val local = address.getAddressLine(0) ?: "Unknown Locality"
                val city = address.locality ?: "Unknown City"
                val state = address.adminArea ?: "Unknown State"
                val country = address.countryName ?: "Unknown Country"

                val fullLocation = "$local $city, $state, $country"

                CurrentLocation.location?.lat = latitude.toString()
                CurrentLocation.location?.lon= longitude.toString()
                CurrentLocation.location?.display_name= fullLocation

                CallApiForHourly(latitude.toString(), longitude.toString())

                CallApiForWeekly(latitude.toString(), longitude.toString())


                binding.currentLocation.text=fullLocation

//                Toast.makeText(this, "Current location: $fullLocation", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "Could not find address for this location.", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            // Geocoder can fail if there's no internet connection or other issues.
            Toast.makeText(this, "Geocoder service unavailable. Please check your internet connection.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

}