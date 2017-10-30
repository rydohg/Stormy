package com.rydohg.kweather.utils

import java.text.SimpleDateFormat
import java.util.*

fun kelvinToCelsius(temp: Int): Int {
    return temp - 273
}

fun kelvinToFahrenheit(temp: Int): Int {
    return (kelvinToCelsius(temp) * 1.8 + 32).toInt()
}

fun unixToDate(datetime: Long): Date {
    return java.util.Date(datetime * 1000)
}

fun unixToFormattedDate(datetime: Long): String {
    val date = unixToDate(datetime)
    return SimpleDateFormat("EEEE, MM/dd/YY", Locale.US).format(date)
}