package com.example.pr222

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.but)
        loginEditText = findViewById(R.id.login)
        passwordEditText = findViewById(R.id.password)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val savedLogin = sharedPreferences.getString("login", null)
        val savedPassword = sharedPreferences.getString("password", null)

        loginEditText.setText(savedLogin)
        passwordEditText.setText(savedPassword)

        button.setOnClickListener {
            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (login.isNotEmpty() && password.isNotEmpty()) {

                val editor = sharedPreferences.edit()
                editor.putString("login", login)
                editor.putString("password", password)
                editor.apply()

                val intent = Intent(this, exchange_rates::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Пожалуйста, введите логин и пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
