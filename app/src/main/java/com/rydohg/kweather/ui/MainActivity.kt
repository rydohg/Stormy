package com.rydohg.kweather.ui

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import android.support.v7.widget.DividerItemDecoration
import android.widget.ImageView
import android.widget.TextView
import com.rydohg.kweather.utils.JsonParser
import com.rydohg.kweather.R
import com.rydohg.kweather.utils.imageFromDesc
import com.rydohg.kweather.utils.kelvinToFahrenheit
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkAsyncTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class NetworkAsyncTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String {
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url("http://api.openweathermap.org/data/2.5/forecast/daily?zip=32904,us&appid=829aad9b0941ffc8fbb7a9d2ad9c334c")
                    .build()
            val response = client.newCall(request).execute()

            return response.body()!!.string()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d("KWeatherMain", result)
            val parser = JsonParser(result)

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

            recyclerView.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(parent)
            recyclerView.layoutManager = mLayoutManager

            val mDividerItemDecoration = DividerItemDecoration(
                    recyclerView.context,
                    mLayoutManager.orientation
            )

            recyclerView.addItemDecoration(mDividerItemDecoration)

            recyclerView.adapter = WeatherAdapter(parser.parsedForecasts)

            val todayForecast = parser.parsedForecasts[0]

            val todayImageView = findViewById<ImageView>(R.id.todayImageView)
            val minMaxTextView = findViewById<TextView>(R.id.min_max_text_view)

            val todayForecastString = kelvinToFahrenheit(todayForecast.maxTempKelvin).toString() + "/" + kelvinToFahrenheit(todayForecast.minTempKelvin)
            minMaxTextView.text = todayForecastString

            todayImageView.setImageDrawable(imageFromDesc(todayForecast.desc))
        }
    }
}
