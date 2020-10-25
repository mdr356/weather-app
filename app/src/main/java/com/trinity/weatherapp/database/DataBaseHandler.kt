package com.trinity.weatherapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.trinity.weatherapp.model.WeatherMapResponse

val DATABASENAME = "MY DATABASE"
val TABLENAME = "weather1"
val COL_ID = "id"
val COL_TEMP = "temp"
val COL_FEELS_LIKE = "feelslike"
val COL_PRESSURE = "pressure"
val COL_HUMIDITY = "humidity"
val COL_LONGI= "longitude"
val COL_LATI = "latitude"
val COL_ZIP_CODE = "zipcode"
val COL_ADDRESS = "address"
class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLENAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TEMP + " VARCHAR(256)," +
                COL_FEELS_LIKE + " VARCHAR(256)," +
                COL_PRESSURE + " VARCHAR(256)," +
                COL_HUMIDITY + " VARCHAR(256),"+
                COL_LONGI + " VARCHAR(256)," +
                COL_LATI + " VARCHAR(256)," +
                COL_ZIP_CODE + " VARCHAR(256)," +
                COL_ADDRESS + " VARCHAR(256)" +
                ")"

        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }
    fun insertData(response: WeatherMapResponse, longitude: String, latitude: String, zipcode: String, address: String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID, 0)
        contentValues.put(COL_TEMP, response.main?.temp ?: 0.00)
        contentValues.put(COL_FEELS_LIKE, response.main?.feelsLike ?: 0.00)
        contentValues.put(COL_PRESSURE, response.main?.pressure ?: 0)
        contentValues.put(COL_HUMIDITY, response.main?.humidity ?: 0)
        contentValues.put(COL_LONGI, longitude  ?: "0.00")
        contentValues.put(COL_LATI, latitude  ?: "0.00")
        contentValues.put(COL_ZIP_CODE, zipcode)
        contentValues.put(COL_ADDRESS, address)
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
                response.pressure = result.getString(result.getColumnIndex(COL_PRESSURE)).toInt()
                response.humidity = result.getString(result.getColumnIndex(COL_HUMIDITY)).toInt()
                response.longitude = result.getString(result.getColumnIndex(COL_LONGI)).toDouble()
                response.latitude = result.getString(result.getColumnIndex(COL_LATI)).toDouble()
                response.zipcode = result.getString(result.getColumnIndex(COL_ZIP_CODE))
                response.address = result.getString(result.getColumnIndex(COL_ADDRESS))
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
        var latitude: Double? = null,
        var zipcode: String? = null,
        var address: String? = null
    )
}