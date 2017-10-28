package com.rydohg.kweather

import org.json.JSONObject

// OpenWeatherMap returns temp in Kelvin and pressure in hPa
data class Forecast(val cityName: String, val maxTempKelvin: Int, val minTempKelvin: Int)

class JsonParser(response: String?) {
    val cityName: String?
    val parsedForecasts: ArrayList<Forecast>

    init {
        val jsonObject = JSONObject(response)
        cityName = jsonObject.getJSONObject("city").getString("name")
        val rawForecastList = jsonObject.getJSONArray("list")

        parsedForecasts = ArrayList()

        for (i in 0 until rawForecastList.length()) {
            val jsonForecast = rawForecastList.getJSONObject(i)
            val temperatures = jsonForecast.getJSONObject("temp")
            val forecast = Forecast(cityName, temperatures.getInt("max"), temperatures.getInt("min"))
            parsedForecasts.add(forecast)
        }
    }
}