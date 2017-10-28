package com.rydohg.kweather

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherDB: SQLiteDatabase = WeatherDBSQLiteHelper(this).writableDatabase
        /*val sampleValues = ContentValues()

        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val now = Date()
        val dateString = dateFormat.format(now)

        sampleValues.put(WeatherDBContract.ForecastEntry.COLUMN_ZIP_CODE, "32904")
        sampleValues.put(WeatherDBContract.ForecastEntry.COLUMN_DATE, dateString)
        sampleValues.put(WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP, "90F")
        sampleValues.put(WeatherDBContract.ForecastEntry.COLUMN_LOW_TEMP, "72F")
        sampleValues.put(WeatherDBContract.ForecastEntry.COLUMN_PRESSURE, "1")

        weatherDB.insert(WeatherDBContract.ForecastEntry.TABLE_NAME, null, sampleValues)

        Toast.makeText(this, "Values Inserted", Toast.LENGTH_SHORT).show()*/

        NetworkAsyncTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class NetworkAsyncTask: AsyncTask<Void, Void, String>() {
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
            val textView = findViewById<TextView>(R.id.test_text_view)
            val parser = JsonParser(result)
            val forecast = parser.parsedForecasts[0]

            val maxTemp = kelvinToFahrenheit(forecast.maxTempKelvin).toString() + "°F"
            textView.text = maxTemp
        }
    }

    /*fun readDB(){
        val database = WeatherDBSQLiteHelper(this).readableDatabase

        val projection = arrayOf(
                BaseColumns._ID,
                WeatherDBContract.ForecastEntry.COLUMN_ZIP_CODE,
                WeatherDBContract.ForecastEntry.COLUMN_DATE,
                WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP,
                WeatherDBContract.ForecastEntry.COLUMN_LOW_TEMP,
                WeatherDBContract.ForecastEntry.COLUMN_PRESSURE
        )

        val selection =
                WeatherDBContract.ForecastEntry.COLUMN_DATE + " AND " +
                WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP + " AND " +
                WeatherDBContract.ForecastEntry.COLUMN_ZIP_CODE

        val cursor = database.query(
                WeatherDBContract.ForecastEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        )

        cursor.moveToFirst()
        toast(cursor.count.toString())
        toast(cursor.getString(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_DATE)))
        toast(cursor.getString(cursor.getColumnIndex(WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP)))
        cursor.close()
    }

    private fun toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }*/
}
