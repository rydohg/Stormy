package com.rydohg.kweather.utils

import android.util.Log
import org.json.JSONObject
import java.io.Serializable

//TODO: Pass forecast to activity using JSON and GSON, not Serializable
data class Forecast(val cityName: String,
                    val datetime: Long,
                    val desc: String,
                    val iconName: String,
                    val precipType: String,
                    val precipProbability: Double,
                    val maxTempCelsius: Double,
                    val minTempCelsius: Double) : Serializable

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
            val probability = jsonForecast.getDouble("precipProbability")
            var precipType = String()
            if (jsonForecast.has("precipType")) {
                precipType = jsonForecast.getString("precipType")
            } else {
                Log.d("JSONParser", i.toString())
            }
            val description = jsonForecast.getString("summary")
            val iconString = jsonForecast.getString("icon")

            val forecast = Forecast(
                    cityName,
                    jsonForecast.getLong("time"),
                    description,
                    iconString,
                    precipType,
                    probability,
                    high,
                    low
            )

            parsedForecasts.add(forecast)
        }
    }
}