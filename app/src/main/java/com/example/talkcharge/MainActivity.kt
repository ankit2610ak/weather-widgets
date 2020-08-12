package com.example.talkcharge

import android.Manifest
import android.annotation.SuppressLint
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
import java.math.BigDecimal
import java.util.*
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
                        "b426a7540d88be5d89c501c685cee1e7")
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
                getMaxMinTempForAllDays(arrayList)
                getTodayWeatherAndTemperature(arrayList)

            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun getTodayWeatherAndTemperature(arrayList: ArrayList<WeatherList>) {
        binding.tempTextView.text =
            "" + arrayList[0].main.temp.subtract(BigDecimal(273.15)).toInt() + "°C"

        binding.weatherTextView.text = arrayList[0].weather[0].main + " " +
                arrayList[0].main.temp_max.subtract(BigDecimal(273.15)).toInt() + " / " +
                arrayList[0].main.temp_min.subtract(BigDecimal(273.15)).toInt() + "°C"
    }

    private fun getMaxMinTempForAllDays(arrayList: ArrayList<WeatherList>) {
        binding.tempMaxToday.text = arrayList[0].main.temp_max.toPlainString()
        binding.tempMinToday.text = arrayList[0].main.temp_min.toPlainString()
        binding.tempMaxDay1.text = arrayList[8].main.temp_max.toPlainString()
        binding.tempMinDay1.text = arrayList[8].main.temp_min.toPlainString()
        binding.tempMaxDay2.text = arrayList[15].main.temp_max.toPlainString()
        binding.tempMinDay2.text = arrayList[15].main.temp_min.toPlainString()
        binding.tempMaxDay3.text = arrayList[23].main.temp_max.toPlainString()
        binding.tempMinDay3.text = arrayList[23].main.temp_min.toPlainString()
        binding.tempMaxDay4.text = arrayList[31].main.temp_max.toPlainString()
        binding.tempMinDay4.text = arrayList[31].main.temp_min.toPlainString()
        binding.tempMaxDay5.text = arrayList[39].main.temp_max.toPlainString()
        binding.tempMinDay5.text = arrayList[39].main.temp_min.toPlainString()
    }

    private fun setWeatherFields(arrayList: ArrayList<WeatherList>) {
        binding.grndLevel.text = arrayList[0].main.grnd_level.toString()
        binding.humidity.text = arrayList[0].main.humidity.toString()
        binding.pressure.text = arrayList[0].main.pressure.toString()
        binding.seaLevel.text = arrayList[0].main.sea_level.toString()
        binding.wind.text = arrayList[0].wind.speed.toPlainString()
    }

}