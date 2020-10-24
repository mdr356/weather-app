package com.trinity.weatherapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.trinity.weatherapp.model.WeatherMapResponse

val DATABASENAME = "MY DATABASE"
val TABLENAME = "weather"
val COL_ID = "id"
val COL_TEMP = "temp"
val COL_FEELS_LIKE = "feelslike"
val COL_TEMP_MIN = "min"
val COL_TEMP_MAX = "max"
val COL_PRESSURE = "pressure"
val COL_HUMIDITY = "humidity"
val COL_LONGI= "longitude"
val COL_LATI = "latitude"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLENAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TEMP + " VARCHAR(256)," +
                COL_FEELS_LIKE + " VARCHAR(256)," +
                COL_TEMP_MIN + " VARCHAR(256)," +
                COL_TEMP_MAX + " VARCHAR(256)," +
                COL_PRESSURE + " VARCHAR(256)," +
                COL_HUMIDITY + " VARCHAR(256)"

        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }
    fun insertData(response: WeatherMapResponse, longitude: String, latitude: String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID, 0)
        contentValues.put(COL_TEMP, response.main?.temp)
        contentValues.put(COL_FEELS_LIKE, response.main?.feelsLike)
        contentValues.put(COL_TEMP_MIN, response.main?.tempMin)
        contentValues.put(COL_TEMP_MAX, response.main?.tempMax)
        contentValues.put(COL_PRESSURE, response.main?.pressure)
        contentValues.put(COL_HUMIDITY, response.main?.humidity)
        contentValues.put(COL_LONGI, longitude)
        contentValues.put(COL_LATI, latitude)
        val result = database.insert(TABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }
    fun readData(): MutableList<TempResponse> {
        val list: MutableList<TempResponse> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLENAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val response = TempResponse()
                //response.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                response.temp = result.getString(result.getColumnIndex(COL_TEMP)).toDouble()
                response.feelsLike = result.getString(result.getColumnIndex(COL_FEELS_LIKE)).toDouble()
                response.tempMin = result.getString(result.getColumnIndex(COL_TEMP_MIN)).toDouble()
                response.tempMax = result.getString(result.getColumnIndex(COL_TEMP_MAX)).toDouble()
                response.pressure = result.getString(result.getColumnIndex(COL_PRESSURE)).toInt()
                response.humidity = result.getString(result.getColumnIndex(COL_HUMIDITY)).toInt()
                response.longitude = result.getString(result.getColumnIndex(COL_LONGI)).toDouble()
                response.latitude = result.getString(result.getColumnIndex(COL_LATI)).toDouble()
                list.add(response)
            }
            while (result.moveToNext())
        }
        return list
    }

    data class TempResponse (
        var temp: Double? = null,
        var feelsLike: Double? = null,
        var tempMin: Double? = null,
        var tempMax: Double? = null,
        var pressure: Int? = null,
        var humidity: Int? = null,
        var longitude: Double? = null,
        var latitude: Double? = null
    )
}