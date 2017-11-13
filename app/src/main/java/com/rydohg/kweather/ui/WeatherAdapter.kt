package com.rydohg.kweather.ui

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.rydohg.kweather.R
import com.rydohg.kweather.utils.Forecast
import com.rydohg.kweather.utils.imageFromDesc
import com.rydohg.kweather.utils.unixToFormattedDate


class WeatherAdapter constructor(private val forecastList: ArrayList<Forecast>) : RecyclerView.Adapter<WeatherAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
        val layout = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_forecast, parent, false) as ConstraintLayout
        return CustomViewHolder(layout)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val forecast = forecastList[position]

        val highTempString = forecast.maxTempCelsius.toString() + "°F"
        val lowTempString = forecast.minTempCelsius.toString() + "°F"
        val desc = forecast.desc
        val date = unixToFormattedDate(forecast.datetime)

        holder.highTempTextView.text = highTempString
        holder.lowTempTextView.text = lowTempString
        holder.desc.text = desc
        holder.date.text = date
        holder.image.setImageDrawable(imageFromDesc(forecast.iconName))
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    inner class CustomViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var highTempTextView: TextView = rootView.findViewById(R.id.high_temp_text_view)
        var lowTempTextView: TextView = rootView.findViewById(R.id.low_temp_text_view)
        var desc: TextView = rootView.findViewById(R.id.desc_text_view)
        var date: TextView = rootView.findViewById(R.id.date_text_view)
        var image: ImageView = rootView.findViewById(R.id.weather_image_view)
    }
}
