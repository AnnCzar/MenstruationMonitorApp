package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginWindowActivity : AppCompatActivity(){
    private lateinit var enterPassword: EditText
    private lateinit var enterLogin: EditText
    private lateinit var buttonConfirmLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_window)

        // znalezienie elementów
        enterLogin = findViewById(R.id.enterLogin)
        enterPassword = findViewById(R.id.enterPassword)
        buttonConfirmLogin = findViewById(R.id.buttonConfirmLogin)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        buttonConfirmLogin.setOnClickListener {
            openMainWindowPeriodActivity()
        }
    }

    private fun openMainWindowPeriodActivity() {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        startActivity(intent)
    }


}
