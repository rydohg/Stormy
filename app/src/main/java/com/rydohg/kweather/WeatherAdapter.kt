package com.rydohg.kweather

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View


class WeatherAdapter constructor(private val forecastList: ArrayList<Forecast>): RecyclerView.Adapter<WeatherAdapter.CustomViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
        val layout = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_forecast, parent, false) as ConstraintLayout

        return CustomViewHolder(layout)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val forecast = forecastList[position]

        val highTempString = kelvinToFahrenheit(forecast.maxTempKelvin).toString() + "°F"
        val lowTempString = kelvinToFahrenheit(forecast.minTempKelvin).toString() + "°F"
        val cityName = forecast.cityName

        holder.highTempTextView.text = highTempString
        holder.lowTempTextView.text = lowTempString
        holder.cityName.text = cityName
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    inner class CustomViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var highTempTextView: TextView = rootView.findViewById(R.id.high_temp_text_view)
        var lowTempTextView: TextView = rootView.findViewById(R.id.low_temp_text_view)
        var cityName: TextView = rootView.findViewById(R.id.city_name_text_view)
    }

}
