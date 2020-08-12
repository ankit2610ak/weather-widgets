package com.example.talkcharge.model

data class WeatherList(
    var id: Int,
    val main: Main,
    val weather: ArrayList<WeatherCloud>,
    val dt_txt: String

)