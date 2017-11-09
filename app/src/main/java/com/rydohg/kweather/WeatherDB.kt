package com.rydohg.kweather

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object WeatherDBContract {
    val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ForecastEntry.TABLE_NAME + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ForecastEntry.COLUMN_ZIP_CODE + " TEXT, " +
            ForecastEntry.COLUMN_HIGH_TEMP + " DOUBLE, " +
            ForecastEntry.COLUMN_LOW_TEMP + " DOUBLE, " +
            ForecastEntry.COLUMN_PRESSURE + " TEXT, " +
            ForecastEntry.COLUMN_DESC + " TEXT, " +
            ForecastEntry.COLUMN_ICON_NAME + " TEXT, " +
            ForecastEntry.COLUMN_DATE + " TEXT" + ")"

    class ForecastEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "weather"
            val COLUMN_ZIP_CODE = "zip_code"
            val COLUMN_DATE = "date"
            val COLUMN_HIGH_TEMP = "high"
            val COLUMN_LOW_TEMP = "low"
            val COLUMN_PRESSURE = "atm"
            val COLUMN_DESC = "desc"
            val COLUMN_ICON_NAME = "icon_name"

            val projection = arrayOf(BaseColumns._ID,
                    WeatherDBContract.ForecastEntry.COLUMN_DATE,
                    WeatherDBContract.ForecastEntry.COLUMN_HIGH_TEMP,
                    WeatherDBContract.ForecastEntry.COLUMN_LOW_TEMP,
                    WeatherDBContract.ForecastEntry.COLUMN_ZIP_CODE,
                    WeatherDBContract.ForecastEntry.COLUMN_DESC,
                    WeatherDBContract.ForecastEntry.COLUMN_ICON_NAME,
                    WeatherDBContract.ForecastEntry.COLUMN_PRESSURE
            )
        }
    }
}

class WeatherDBSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(WeatherDBContract.CREATE_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherDBContract.ForecastEntry.TABLE_NAME)
        onCreate(sqLiteDatabase)
    }

    companion object {
        private val DATABASE_VERSION = 5
        val DATABASE_NAME = "weather_db"
    }
}