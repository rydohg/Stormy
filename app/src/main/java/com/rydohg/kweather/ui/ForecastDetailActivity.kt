package com.rydohg.kweather.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.rydohg.kweather.R
import com.rydohg.kweather.utils.Forecast

class ForecastDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast_detail)

        val intent = this.intent
        val forecast = intent.extras.getSerializable("forecast") as Forecast

        val maxTextView: TextView = findViewById(R.id.max_text_view)
        val minTextView: TextView = findViewById(R.id.min_text_view)
        val precipTextView: TextView = findViewById(R.id.precip_text_view)

        maxTextView.text = forecast.maxTempCelsius.toInt().toString()
        minTextView.text = forecast.minTempCelsius.toInt().toString()
        precipTextView.text = "Chance of " + forecast.precipType.capitalize() + ": " + (forecast.precipProbability * 100).toInt().toString() + "%"
    }
}
