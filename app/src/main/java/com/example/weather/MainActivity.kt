package com.example.weather

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    data class WeatherData(
        @SerializedName("sys") val sys: Sys,
        @SerializedName("name") val cityName: String,
        @SerializedName("main") val main: Main,
        @SerializedName("wind") val wind: Wind
    )

    data class Sys(
        @SerializedName("sunrise") val sunrise: Long,
        @SerializedName("sunset") val sunset: Long
    )

    data class Main(
        @SerializedName("temp") val temperature: Double
    )

    data class Wind(
        @SerializedName("speed") val windSpeed: Double
    )

    public fun onClick(v: View) {
        val cityEditText: EditText = findViewById(R.id.cityEditText)
        val cityName = cityEditText.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val apiKey = "0cae94396adb869c5bc9e251426292be"
                val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$apiKey&units=metric"
                val stream = URL(weatherURL).openStream()
                val data = stream.bufferedReader().use { it.readText() }

                val gson = Gson()
                val weatherData = gson.fromJson(data, WeatherData::class.java)

                val sunriseTime = weatherData.sys.sunrise * 1000
                val sunsetTime = weatherData.sys.sunset * 1000

                val sunrise = Date(sunriseTime)
                val sunset = Date(sunsetTime)

                val daylightHours = (sunset.time - sunrise.time) / (1000 * 60 * 60)
                val daylightMinutes = ((sunset.time - sunrise.time) % (1000 * 60 * 60)) / (1000 * 60)

                val temperature = weatherData.main.temperature
                val windSpeed = weatherData.wind.windSpeed

                val daylightTextView: TextView = findViewById(R.id.daylightTextView)
                val temperatureTextView: TextView = findViewById(R.id.temperatureTextView)
                val windSpeedTextView: TextView = findViewById(R.id.windSpeedTextView)

                val daylight = "Daylight: $daylightHours h $daylightMinutes min"
                val temperatureText = "Temperature: $temperature °C"
                val windSpeedText = "Wind Speed: $windSpeed m/s"

                runOnUiThread {
                    daylightTextView.text = daylight
                    temperatureTextView.text = temperatureText
                    windSpeedTextView.text = windSpeedText
                }

                Log.d("WeatherInfo", "City: ${weatherData.cityName}")
                Log.d("WeatherInfo", "Temperature: $temperature °C")
                Log.d("WeatherInfo", "Wind Speed: $windSpeed m/s")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
