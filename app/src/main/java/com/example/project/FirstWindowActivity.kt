package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FirstWindowActivity : AppCompatActivity() {
    // definiowanie zmiennych, do których później przypisujemy odpowiendie elementy z layoutu
    private lateinit var button_log: Button
    private lateinit var button_reg: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_window)

        // znalezienie przycisków
        button_log = findViewById(R.id.button_signin)
        button_reg = findViewById(R.id.button_register)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        button_log.setOnClickListener {
            openLoginWindowActivity()
        }
        button_reg.setOnClickListener {
            openRegisterWindowActivity()
        }

    }
    // Metoda do otwierania drugiej aktywności
    private fun openLoginWindowActivity() {
        val intent = Intent(this, LoginWindowActivity::class.java)
        startActivity(intent)
    }
    private fun openRegisterWindowActivity() {
        val intent = Intent(this, RegisterWindowActivity::class.java)
        startActivity(intent)
    }

}


