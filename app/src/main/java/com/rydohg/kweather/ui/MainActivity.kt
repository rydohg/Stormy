package com.rydohg.kweather.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceActivity
import android.provider.BaseColumns
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
import com.rydohg.kweather.WeatherDBContract
import com.rydohg.kweather.WeatherDBSQLiteHelper
import com.rydohg.kweather.utils.Forecast
import com.rydohg.kweather.utils.JsonParser
import com.rydohg.kweather.utils.imageFromDesc
import com.rydohg.kweather.utils.unixToDate
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!areForecastsRecent()) {
            NetworkAsyncTask().execute()
        } else {

        }

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

    private fun areForecastsRecent(): Boolean {
        val db = WeatherDBSQLiteHelper(this).readableDatabase

        val projection = arrayOf(BaseColumns._ID,
                WeatherDBContract.ForecastEntry.COLUMN_DATE,
                WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP,
                WeatherDBContract.ForecastEntry.COLUMN_LOW_TEMP,
                WeatherDBContract.ForecastEntry.COLUMN_ZIP_CODE,
                WeatherDBContract.ForecastEntry.COLUMN_PRESSURE
        )

        val cursor = db.query(WeatherDBContract.ForecastEntry.TABLE_NAME, projection, null, null, null, null, null)
        cursor.moveToFirst()
        val datetime = cursor.getLong(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_DATE))

        val currentDate = Calendar.getInstance()
        val latestForecast = Calendar.getInstance()
        latestForecast.time = unixToDate(datetime)

        cursor.close()

        return currentDate.get(Calendar.DAY_OF_MONTH) == latestForecast.get(Calendar.DAY_OF_MONTH)
    }

    private fun populateRecyclerView(result: String?) {
        val parser = JsonParser(result)

        writeToDB(parser.parsedForecasts)

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

    private fun writeToDB(result: ArrayList<Forecast>) {
        val db = WeatherDBSQLiteHelper(this).writableDatabase
        for (i in result) {
            val values = ContentValues()
            values.put(WeatherDBContract.ForecastEntry.COLUMN_DATE, i.datetime)
            // TODO: Make database store them as ints, not strings
            values.put(WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP, i.maxTempCelsius.toString())
            values.put(WeatherDBContract.ForecastEntry.COLUMN_LOW_TEMP, i.minTempCelsius.toString())
            values.put(WeatherDBContract.ForecastEntry.COLUMN_PRESSURE, "1")
            values.put(WeatherDBContract.ForecastEntry.COLUMN_ZIP_CODE, "32904")

            db.insert(WeatherDBContract.ForecastEntry.TABLE_NAME, null, values)
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

            populateRecyclerView(result)
        }
    }
}
