package com.rydohg.kweather.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import android.view.View
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
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!areForecastsRecent() && isNetworkAvailable()) {
            NetworkAsyncTask().execute()
        } else {
            Log.d("MainActivity", "Loading from DB")
            val db = WeatherDBSQLiteHelper(this).readableDatabase
            val cursor = db.query(WeatherDBContract.ForecastEntry.TABLE_NAME, WeatherDBContract.ForecastEntry.projection, null, null, null, null, null)

            val forecasts = ArrayList<Forecast>()

            cursor.moveToFirst()

            do {
                val high = cursor.getDouble(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP))
                val low = cursor.getDouble(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_LOW_TEMP))
                val date = cursor.getLong(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_DATE))
                val desc = cursor.getString(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_DESC))
                val iconName = cursor.getString(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_ICON_NAME))
                /*val pressure = cursor.getString(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_PRESSURE))
                val zipCode = cursor.getString(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_ZIP_CODE))*/

                forecasts.add(Forecast("West Melbourne", date, desc, iconName, high, low))
            } while (cursor.moveToNext())

            populateRecyclerView(forecasts)
            cursor.close()
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun areForecastsRecent(): Boolean {
        val db = WeatherDBSQLiteHelper(this).readableDatabase

        val cursor = db.query(WeatherDBContract.ForecastEntry.TABLE_NAME, WeatherDBContract.ForecastEntry.projection, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val datetime = cursor.getLong(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_DATE))

            val currentDate = Calendar.getInstance()
            val latestForecast = Calendar.getInstance()
            latestForecast.time = unixToDate(datetime)

            return currentDate.get(Calendar.DAY_OF_MONTH) == latestForecast.get(Calendar.DAY_OF_MONTH)
        }
        cursor.close()
        Log.d("MainActivity", "Forecasts aren't recent")
        return false
    }

    private fun populateRecyclerView(result: String?) {
        val parser = JsonParser(result)
        populateRecyclerView(parser.parsedForecasts)
    }

    private fun populateRecyclerView(result: ArrayList<Forecast>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(parent)
        recyclerView.layoutManager = mLayoutManager

        val mDividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                mLayoutManager.orientation
        )

        recyclerView.addItemDecoration(mDividerItemDecoration)

        recyclerView.adapter = WeatherAdapter(result)

        recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(this, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val intent = Intent(applicationContext, ForecastDetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("forecast", result[position])
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {

                    }
                })
        )
        val todayForecast = result[0]

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
            values.put(WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP, i.maxTempCelsius)
            values.put(WeatherDBContract.ForecastEntry.COLUMN_LOW_TEMP, i.minTempCelsius)
            values.put(WeatherDBContract.ForecastEntry.COLUMN_DESC, i.desc)
            values.put(WeatherDBContract.ForecastEntry.COLUMN_ICON_NAME, i.iconName)
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

            writeToDB(JsonParser(result).parsedForecasts)

            populateRecyclerView(result)
        }
    }
}
