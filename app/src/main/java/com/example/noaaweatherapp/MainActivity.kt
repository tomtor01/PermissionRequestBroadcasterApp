package com.example.noaaweatherapp

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



        fetchButton.setOnClickListener {
            val city = citySpinner.selectedItem.toString()
            val date = datePicker.text.toString()

            if (city.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Koniecznie wybierz miasto i datę.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stationId = getStationIdForCity(city)

            if (stationId != null) {
                fetchTemperature(stationId, date, resultTextView)
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

    private fun fetchTemperature(stationId: String, date: String, resultTextView: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiToken = "QEpLxBhqskpsnHzdCFUbhPfgMgEdCaeo"
                val client = Client(apiToken)

                // Fetch TMAX, TMIN, and TAVG data
                val tmax = client.getHistoricalTemperature(stationId, date, "TMAX")
                val tmin = client.getHistoricalTemperature(stationId, date, "TMIN")
                val tavg = client.getHistoricalTemperature(stationId, date, "TAVG")

                withContext(Dispatchers.Main) {
                    // Update the TextView with all temperature values
                    val resultText = buildString {
                        append("Data: $date\n")
                        tmax?.let { append("Temp maksymalna: ${it.value / 10.0}°C\n") }
                        tmin?.let { append("Temp minimalna: ${it.value / 10.0}°C\n") }
                        tavg?.let { append("Temp średnia: ${it.value / 10.0}°C\n") }
                    }

                    resultTextView.text = resultText
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Failed to fetch data: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private var selectedDate = Calendar.getInstance() // Store the last selected date

    fun showDatePicker(view: View) {
        // Use the previously selected date as the default
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                // Update the stored selected date
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                // Format the selected date as YYYY-MM-DD
                val formattedMonth = (selectedMonth + 1).toString().padStart(2, '0') // Pad single digit months
                val formattedDay = selectedDay.toString().padStart(2, '0') // Pad single digit days
                val formattedDate = "$selectedYear-$formattedMonth-$formattedDay"

                // Update the TextView with the selected date
                val datePickerTextView: TextView = findViewById(R.id.date_picker)
                datePickerTextView.text = formattedDate
            },
            year, month, dayOfMonth
        )
        // Restrict the user from selecting future dates
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Show the DatePickerDialog
        datePickerDialog.show()
    }
}