package com.example.pr222

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class exchange_rates : AppCompatActivity() {

    private lateinit var convertButton: Button
    private lateinit var nextButton: Button
    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var output: TextView
    private lateinit var dateTextView: TextView
    private lateinit var firstCurrencyRateTextView: TextView
    private lateinit var secondCurrencyRateTextView: TextView

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_rates)

        dateTextView = findViewById(R.id.datatime)
        convertButton = findViewById(R.id.buttons)
        nextButton = findViewById(R.id.next)
        spinner1 = findViewById(R.id.spinner1)
        spinner2 = findViewById(R.id.spinner2)
        output = findViewById(R.id.textView)
        firstCurrencyRateTextView = findViewById(R.id.firstCurrencyRateTextView)
        secondCurrencyRateTextView = findViewById(R.id.secondCurrencyRateTextView)

        val currencies = arrayOf("GBP", "EUR", "JPY", "RUB", "USD", "BTC")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter
        spinner2.adapter = adapter

        val sdf = SimpleDateFormat("dd/M/yyyy || hh:mm")
        val currentDate = sdf.format(Date())
        dateTextView.text = currentDate

        nextButton.setOnClickListener {
            val intent = Intent(this, data_base::class.java)
            startActivity(intent)
        }

        convertButton.setOnClickListener {
            val currency1 = spinner1.selectedItem.toString()
            val currency2 = spinner2.selectedItem.toString()

            if (currency1 == currency2) {
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Выбраны одинаковые валюты", Snackbar.LENGTH_LONG)
                snackbar.setAction("Очистить") {
                    clearSelections()
                }.show()
            } else {
                getRates()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getRates() {
        val currency1 = spinner1.selectedItem.toString()
        val currency2 = spinner2.selectedItem.toString()

        val currencyPair1 = if (currency1 == "RUB") "RUB$currency2" else currency1 + "RUB"
        val currencyPair2 = if (currency2 == "RUB") "RUB$currency1" else currency2 + "RUB"

        val apiKey = "89e019ecd67dbe0cd8a4835084d81f0c"
        val url = "https://currate.ru/api/?get=rates&pairs=$currencyPair1,$currencyPair2&key=$apiKey"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val obj = JSONObject(response)
                    val data = obj.getJSONObject("data")
                    val rate1 = data.getDouble(currencyPair1)
                    val rate2 = data.getDouble(currencyPair2)

                    firstCurrencyRateTextView.text = "Курс $currency1 в RUB: $rate1"
                    secondCurrencyRateTextView.text = "Курс $currency2 в RUB: $rate2"

                    val conversionResult = when {
                        currency1 == "RUB" -> 1 / rate2
                        currency2 == "RUB" -> rate1
                        else -> rate1 / rate2
                    }

                    val formattedConversionResult = String.format("%.1f", conversionResult)
                    output.text = "Результат: 1 $currency1 = $formattedConversionResult $currency2"

                } catch (e: Exception) {
                    Log.d("MyLog", "Ошибка парсинга: $e")
                    output.text = "Ошибка парсинга"

                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Попробуйте снова", Snackbar.LENGTH_LONG)
                    snackbar.setAction("Очистить") {
                        clearSelections()
                    }.show()
                }
            },
            { error ->
                Log.d("MyLog", "Ошибка Volley: $error")
                output.text = "Ошибка получения данных"
            }
        )
        queue.add(stringRequest)
    }

    private fun clearSelections() {

        spinner1.setSelection(0)
        spinner2.setSelection(0)
        output.text = ""
        firstCurrencyRateTextView.text = ""
        secondCurrencyRateTextView.text = ""
    }
}
