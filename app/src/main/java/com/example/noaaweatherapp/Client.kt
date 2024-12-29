package com.example.noaaweatherapp

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class Client(private val apiToken: String) {

    private val baseUrl = "https://www.ncei.noaa.gov/cdo-web/api/v2/data"
    private val client = OkHttpClient()

    fun getHistoricalTemperature(stationId: String, date: String, dataType: String): TemperatureData? {
        val url = "$baseUrl?datasetid=GHCND&stationid=$stationId&startdate=$date&enddate=$date&datatypeid=$dataType"

        val request = Request.Builder()
            .url(url)
            .addHeader("token", apiToken)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Unexpected response: ${response.code}")

            val body = response.body?.string() ?: throw Exception("Response body is null")
            val json = JSONObject(body)

            val results = json.optJSONArray("results")
            if (results != null && results.length() > 0) {
                val result = results.getJSONObject(0)
                return TemperatureData(
                    date = result.getString("date"),
                    value = result.getInt("value")
                )
            }
        }
        return null
    }
}

data class TemperatureData(
    val date: String,
    val value: Int
)
