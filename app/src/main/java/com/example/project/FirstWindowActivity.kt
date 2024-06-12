package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FirstWindowActivity : AppCompatActivity() {
    // definiowanie zmiennych, do których później przypisujemy odpowiendie elementy z layoutu
    private lateinit var button_log: Button
    private lateinit var button_reg: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_window)
        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        // znalezienie przycisków
        button_log = findViewById(R.id.button_signin)
        button_reg = findViewById(R.id.button_register)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        button_log.setOnClickListener {
            openLoginWindowActivity(userId)
        }
        button_reg.setOnClickListener {
            openRegisterWindowActivity(userId)
        }

    }
    // Metoda do otwierania drugiej aktywności
    private fun openLoginWindowActivity(userId: String) {
        val intent = Intent(this, LoginWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun openRegisterWindowActivity(userId: String) {
        val intent = Intent(this, RegisterWindow1Activity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

}


