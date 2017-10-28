package com.rydohg.kweather

fun kelvinToCelsius(temp: Int): Int {
    return temp - 273
}

fun kelvinToFahrenheit(temp: Int): Int {
    return (kelvinToCelsius(temp) * 1.8 + 32).toInt()
}