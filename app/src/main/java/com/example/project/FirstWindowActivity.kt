package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * This activity serves as the first window of the application, providing
 * options for the user to log in or register.
 */
class FirstWindowActivity : AppCompatActivity() {
    // definiowanie zmiennych, do których później przypisujemy odpowiendie elementy z layoutu
    private lateinit var button_log: Button
    private lateinit var button_reg: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    /**
     * Called when the activity is first created. Initializes the layout and sets up click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains the previous data.
     */
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

    /**
     * Opens the LoginWindowActivity.
     *
     * @param userId The ID of the user, passed to the next activity.
     */
    private fun openLoginWindowActivity(userId: String) {
        val intent = Intent(this, LoginWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    /**
     * Opens the RegisterWindow1Activity.
     *
     * @param userId The ID of the user, passed to the next activity.
     */
    private fun openRegisterWindowActivity(userId: String) {
        val intent = Intent(this, RegisterWindow1Activity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

}


