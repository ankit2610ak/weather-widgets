package com.example.talkcharge.model

import java.math.BigDecimal

data class Main(
    val temp: BigDecimal,
    val temp_min: BigDecimal,
    val temp_max: BigDecimal,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int
)
