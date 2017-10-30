package com.rydohg.kweather.utils

import org.json.JSONObject

// OpenWeatherMap returns temp in Kelvin and pressure in hPa
data class Forecast(val cityName: String, val datetime: Long, val desc: String, val maxTempKelvin: Int, val minTempKelvin: Int)

class JsonParser(response: String?) {
    private val cityName: String?
    val parsedForecasts: ArrayList<Forecast>

    init {
        val jsonObject = JSONObject(response)
        cityName = jsonObject.getJSONObject("city").getString("name")
        val rawForecastList = jsonObject.getJSONArray("list")

        parsedForecasts = ArrayList()

        (0 until rawForecastList.length()).forEach { i ->
            val jsonForecast = rawForecastList.getJSONObject(i)
            val temperatures = jsonForecast.getJSONObject("temp")
            val descriptions = jsonForecast.getJSONArray("weather")

            val forecast = Forecast(
                    cityName,
                    jsonForecast.getLong("dt"),
                    descriptions.getJSONObject(0).getString("main"),
                    temperatures.getInt("max"),
                    temperatures.getInt("min")
            )

            parsedForecasts.add(forecast)
        }
    }
}