package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegisterWindowActivity : AppCompatActivity(){
    private lateinit var enterPasswordRegister: EditText
    private lateinit var enterLoginReginster: EditText
    private lateinit var enterLastPeriod: EditText
    private lateinit var cycleLen: EditText
    private lateinit var periodLen: EditText
    private lateinit var medicieInput: EditText
    private lateinit var buttonConfirmRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window)

        // znalezienie elementów
        enterLoginReginster = findViewById(R.id.enterLoginRegister)
        enterPasswordRegister = findViewById(R.id.enterPasswordRegister)
        enterLastPeriod = findViewById(R.id.enterLastPeriod)
        cycleLen = findViewById(R.id.cycleLen)
        periodLen = findViewById(R.id.periodLen)
        medicieInput = findViewById(R.id.medicineInput)
        buttonConfirmRegister = findViewById(R.id.buttonConfirmRegister)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        buttonConfirmRegister.setOnClickListener {
            openMainWindowPeriodActivity()
        }
    }

    private fun openMainWindowPeriodActivity() {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }


}
