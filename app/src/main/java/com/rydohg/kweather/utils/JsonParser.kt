package com.rydohg.kweather.utils

import org.json.JSONObject

data class Forecast(val cityName: String, val datetime: Long, val desc: String, val iconName: String, val maxTempCelsius: Double, val minTempCelsius: Double)

class JsonParser(response: String?) {
    private val cityName: String?
    val parsedForecasts: ArrayList<Forecast>

    init {
        val jsonObject = JSONObject(response)
        // TODO: Get city name from lat and long
        cityName = "West Melbourne, FL"
        val rawForecastList = jsonObject.getJSONObject("daily").getJSONArray("data")

        parsedForecasts = ArrayList()

        (0 until rawForecastList.length()).forEach { i ->
            val jsonForecast = rawForecastList.getJSONObject(i)
            val high = jsonForecast.getDouble("temperatureHigh")
            val low = jsonForecast.getDouble("temperatureLow")
            val description = jsonForecast.getString("summary")
            val iconString = jsonForecast.getString("icon")

            val forecast = Forecast(
                    cityName,
                    jsonForecast.getLong("time"),
                    description,
                    iconString,
                    high,
                    low
            )

            parsedForecasts.add(forecast)
        }
    }
}