package com.example.weatherapp.api

import com.example.weatherapp.model.Forecast
import com.example.weatherapp.model.WeatherForecast
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date


suspend fun createHttpClient(): HttpClient {
    return HttpClient(CIO)
}

suspend fun httpGet(client: HttpClient, url: String): HttpResponse? {
    return try {
        client.get(url)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getFirstWeatherConditions(forecasts: List<Forecast>): List<Forecast> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return forecasts.groupBy { sdf.format(Date(it.dt * 1000)) }
        .values.map { it.first() }
}

suspend fun weatherHandler(latitude: Double?, longitude: Double?, city: String?): List<Forecast>? {
    val client = createHttpClient()
    val gson = Gson()
    val apiKey = "62157b163b26ff96c12df11d42ab57a2"
    val apiUrl = if (city == null) {
        "http://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&appid=$apiKey"
    } else {
        "http://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey"
    }

    val response = httpGet(client, apiUrl) ?: return null
    return if (response.status.value == 200) {
        val weatherForecast = gson.fromJson(response.bodyAsText(), WeatherForecast::class.java)
        getFirstWeatherConditions(weatherForecast.list)
    } else null
}

suspend fun getAQIHandler(latitude: Double?, longitude: Double?, city: String?): Int? {
    val client = createHttpClient()
    val token = "71a722cc195a702b966234354df9867d6eee7d8b"
    val url = if (city != null) {
        "https://api.waqi.info/feed/$city/?token=$token"
    } else {
        "https://api.waqi.info/feed/geo:$latitude;$longitude/?token=$token"
    }

    val response = httpGet(client, url) ?: return null
    return if (response.status.value == 200) {
        val dataObject = JsonParser.parseString(response.bodyAsText()).asJsonObject.getAsJsonObject("data")
        dataObject.get("aqi").asInt
    } else null
}

suspend fun getCoordinatesByCity(city: String): Pair<Double?, Double?> {
    val client = createHttpClient()
    val accessKey = "d4cdef3227b74693397ba6fbe6dd5167"
    val response = httpGet(client, "http://api.positionstack.com/v1/forward?access_key=$accessKey&query=$city") ?: return Pair(null, null)

    return if (response.status.value == 200) {
        val jsonObject = JsonParser.parseString(response.bodyAsText()).asJsonObject
        val latitude = jsonObject.getAsJsonArray("data")[0].asJsonObject.get("latitude").asDouble
        val longitude = jsonObject.getAsJsonArray("data")[0].asJsonObject.get("longitude").asDouble
        Pair(latitude, longitude)
    } else Pair(null, null)
}
