package com.example.noaaweatherapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val citySpinner: Spinner = findViewById(R.id.city_spinner)
        val datePicker: TextView = findViewById(R.id.date_picker)
        val fetchButton: Button = findViewById(R.id.fetch_button)
        val resultTextView: TextView = findViewById(R.id.result_text)

        val apiToken = "QEpLxBhqskpsnHzdCFUbhPfgMgEdCaeo"

        fetchButton.setOnClickListener {
            val city = citySpinner.selectedItem.toString()
            val date = datePicker.text.toString()

            if (city.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please select a city and date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stationId = getStationIdForCity(city)

            if (stationId != null) {
                fetchTemperature(apiToken, stationId, date, resultTextView)
            } else {
                Toast.makeText(this, "No station available for the selected city.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getStationIdForCity(city: String): String? {
        val cityToStationMap = mapOf(
            "New York" to "GHCND:USW00094728",
            "Los Angeles" to "GHCND:USW00023174",
            "Chicago" to "GHCND:USW00094846",
            "Poznań" to "GHCND:PLM00012330",
            "Wrocław" to "GHCND:PLM00012424"
        )
        return cityToStationMap[city]
    }

    @SuppressLint("SetTextI18n")
    private fun fetchTemperature(apiToken: String, stationId: String, date: String, resultTextView: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = Client(apiToken)
                val response = client.getHistoricalTemperature(stationId, date)

                withContext(Dispatchers.Main) {
                    if (response != null) {
                        val temperature = response.value / 10.0
                        resultTextView.text = "Data: $date\nTemperatura MAX: $temperature°C"
                    } else {
                        resultTextView.text = "Brak danych dla wybranej daty."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Failed to fetch data: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun showDatePicker(view: View) {
        // Show the date picker dialog
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                // Add 1 to the month since DatePicker uses 0-based months
                val formattedMonth = (selectedMonth + 1).toString().padStart(2, '0') // Pad single digit months
                val formattedDay = selectedDay.toString().padStart(2, '0') // Pad single digit days

                // Format the date as YYYY-MM-DD
                val formattedDate = "$selectedYear-$formattedMonth-$formattedDay"

                // Update the TextView with the selected date
                val datePickerTextView: TextView = findViewById(R.id.date_picker)
                datePickerTextView.text = formattedDate
            },
            year, month, dayOfMonth
        )
        datePickerDialog.show()
    }
}