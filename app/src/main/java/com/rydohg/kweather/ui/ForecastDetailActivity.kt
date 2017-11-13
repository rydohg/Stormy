package com.rydohg.kweather.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rydohg.kweather.R
import com.rydohg.kweather.utils.Forecast

class ForecastDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast_detail)

        val intent = this.intent
        val forecast = intent.extras.getSerializable("forecast") as Forecast
    }
}
