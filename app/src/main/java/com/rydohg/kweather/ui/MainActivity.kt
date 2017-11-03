package com.rydohg.kweather.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.rydohg.kweather.R
import com.rydohg.kweather.SettingsActivity
import com.rydohg.kweather.utils.JsonParser
import com.rydohg.kweather.utils.imageFromDesc
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkAsyncTask().execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_item -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.WeatherPreferenceFragment::class.java.name)
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class NetworkAsyncTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                        .url("https://api.darksky.net/forecast/b4822f79a3e2eb953e67f91123313d27/28.071672,-80.653603")
                        .build()
                val response = client.newCall(request).execute()

                return response.body()!!.string()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
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

            val todayForecastString = todayForecast.maxTempCelsius.toString() + "/" + todayForecast.minTempCelsius.toString()
            minMaxTextView.text = todayForecastString

            todayImageView.setImageDrawable(imageFromDesc(todayForecast.desc))
        }
    }
}
