@file:Suppress("DEPRECATION")

package com.rydohg.kweather.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import com.rydohg.kweather.MyApplication
import com.rydohg.kweather.R
import java.text.SimpleDateFormat
import java.util.*

/*fun kelvinToCelsius(temp: Int): Int {
    return (temp - 273.15).toInt()
}

fun kelvinToFahrenheit(temp: Double): Int {
    Log.d("Conversion", temp.toString())
    return (1.8 * (temp - 273.15) + 32).toInt()
}*/

fun unixToDate(datetime: Long): Date {
    return java.util.Date(datetime * 1000)
}

fun unixToFormattedDate(datetime: Long): String {
    val date = unixToDate(datetime)
    return SimpleDateFormat("EEEE, MM/dd/YY", Locale.US).format(date)
}

fun imageFromDesc(desc: String): Drawable? {
    val context = MyApplication.applicationContext()
    return when (desc) {
        "rain" -> drawableForId(R.drawable.rain, context)
        "partly-cloudy-day" -> drawableForId(R.drawable.partly_cloudy, context)
        "partly-cloudy-night" -> drawableForId(R.drawable.partly_cloudy, context)
        else -> drawableForId(R.drawable.sun, context)
    }
}

fun drawableForId(id: Int, context: Context): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        context.resources.getDrawable(id, context.theme)
    } else {
        // Deprecated but wouldn't be called on Lollipop or greater
        context.resources.getDrawable(id)
    }
}