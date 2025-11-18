package com.example.weatherforecast.Activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherforecast.Adapters.LocationAdapter
import com.example.weatherforecast.Location.GetLocationResponseItem
import com.example.weatherforecast.OpenWeatherApiService
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivitySearchBinding
import com.example.weatherforecast.isNetworkConnected
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }
    private val adapter= LocationAdapter(this)

    private val BASE_URL="https://nominatim.openstreetmap.org/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.recyclerViewLocation.adapter=adapter

        binding.searchView.post {
            binding.searchView.requestFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.back.setOnClickListener { finish() }
        binding.searchView.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
               CallApi(s.toString().trim())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }


    private fun Internet():Boolean{

        if (isNetworkConnected(this)) {
            return true
        } else {
            return false
        }

    }

    private fun CallApi(value:String) {


        if (!Internet()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        } else {

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherApiService::class.java)

            val response = retrofit.GetLocationResult(value)
                .enqueue(object : Callback<List<GetLocationResponseItem>> {
                    override fun onResponse(
                        call: Call<List<GetLocationResponseItem>?>,
                        response: Response<List<GetLocationResponseItem>?>
                    ) {
                        val result = response.body()
                        if (response.isSuccessful && result != null) {
                            adapter.RefreshData(result)
//                    Toast.makeText(this@SearchActivity,"Data Fetched Successfully", Toast.LENGTH_SHORT).show()
                        } else {

                            Toast.makeText(
                                this@SearchActivity,
                                "Error Fetching data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<List<GetLocationResponseItem>?>,
                        t: Throwable
                    ) {
                        Toast.makeText(this@SearchActivity, t.message, Toast.LENGTH_SHORT).show()
                    }

                })
        }
    }
}