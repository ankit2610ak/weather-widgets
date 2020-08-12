package com.example.talkcharge

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.talkcharge.databinding.ActivityMainBinding
import com.example.talkcharge.model.Weather
import com.example.talkcharge.model.WeatherList
import com.example.talkcharge.retrofit.ApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val TAG = "MainActivity"
    private var lat by Delegates.notNull<Float>()
    private var lon by Delegates.notNull<Float>()
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    Log.d(TAG, "lat: " + location?.latitude)
                    lat = location?.latitude!!.toFloat()
                    lon = location.longitude.toFloat()

                    // Add locality
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1)
                    binding.localityTextView.text = addresses[0].locality


                    getWeatherDetails(
                        lat,
                        lon,
                        "b426a7540d88be5d89c501c685cee1e7"
                    )
                }

        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44
            )
        }

    }

    private fun getWeatherDetails(lat: Float, lon: Float, appId: String) {
        val call: Call<Weather> = ApiClient.getClient.getWeatherForecast(lat, lon, appId)
        call.enqueue(object : retrofit2.Callback<Weather> {
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Log.d(TAG, t.message.toString())
            }

            override fun onResponse(
                call: Call<Weather>,
                response: Response<Weather>
            ) {
                Log.d(TAG, response.body()!!.list[1].dt_txt)
                val arrayList: ArrayList<WeatherList> = response.body()!!.list
                setWeatherFields(arrayList)

            }

        })
    }

    private fun setWeatherFields(arrayList: ArrayList<WeatherList>) {
        binding.grndLevel.text = arrayList[0].main.grnd_level.toString()
        binding.humidity.text = arrayList[0].main.humidity.toString()
        binding.pressure.text = arrayList[0].main.pressure.toString()
        binding.seaLevel.text = arrayList[0].main.sea_level.toString()
        binding.wind.text = arrayList[0].wind.speed.toString()
    }

}