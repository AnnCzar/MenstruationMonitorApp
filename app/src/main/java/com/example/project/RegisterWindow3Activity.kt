package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegisterWindow3Activity : AppCompatActivity(){
    private lateinit var enterMedicineName: EditText
    private lateinit var enterDoseMedicineRegister: EditText
    private lateinit var enterTimeMedicineRegister: EditText


    private lateinit var buttonConfirmRegisterWindow3: Button

    private lateinit var buttonSaveMedicineRegisterWindow3: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window3)

        // znalezienie elementów

        enterMedicineName = findViewById(R.id.enterMedicineName)
        enterDoseMedicineRegister = findViewById(R.id.enterDoseMedicineRegister)
        enterTimeMedicineRegister = findViewById(R.id.enterTimeMedicineRegister)


        buttonConfirmRegisterWindow3 = findViewById(R.id.buttonConfirmRegisterWindow3)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        buttonConfirmRegisterWindow3.setOnClickListener {
            MainWindowPeriodActivity()
        }
    }

    private fun openMainWindowPeriodActivity() {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        startActivity(intent)
    }

}