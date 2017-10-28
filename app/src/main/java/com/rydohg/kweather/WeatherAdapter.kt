package com.rydohg.kweather

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View
import android.widget.ImageView


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
        val desc = forecast.desc
        val date = unixToFormattedDate(forecast.datetime)

        holder.highTempTextView.text = highTempString
        holder.lowTempTextView.text = lowTempString
        holder.desc.text = desc
        holder.date.text = date
        holder.image.setImageDrawable(imageFromDesc(desc))
    }

    private fun imageFromDesc(desc: String): Drawable? {
        val context = MyApplication.applicationContext()
        return when (desc) {
            "Sun" -> drawableForId(R.drawable.sun, context)
            "Rain" -> drawableForId(R.drawable.rain, context)
            else -> drawableForId(R.drawable.sun, context)
        }
    }

    private fun drawableForId(id: Int, context: Context): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(id, context.theme)
        } else {
            context.resources.getDrawable(id)
        }
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
