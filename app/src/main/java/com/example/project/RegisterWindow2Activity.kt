package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegisterWindow2Activity : AppCompatActivity(){
    private lateinit var enterLastPeriod: EditText
    private lateinit var cycleLen: EditText
    private lateinit var periodLen: EditText
    private lateinit var weightRegister: EditText

    //    private lateinit var enterLastPeriod: EditText
//    private lateinit var cycleLen: EditText
//    private lateinit var periodLen: EditText
//    private lateinit var medicieInput: EditText
    private lateinit var buttonConfirmRegisterWindow2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window2)

        // znalezienie elementów

        enterLastPeriod = findViewById(R.id.enterLastPeriod)
        cycleLen = findViewById(R.id.cycleLen)
        periodLen = findViewById(R.id.periodLen)
        weightRegister = findViewById(R.id.weightRegister)

        buttonConfirmRegisterWindow2 = findViewById(R.id.buttonConfirmRegisterWindow2)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        buttonConfirmRegisterWindow2.setOnClickListener {
            openRegisterWindow3Activity()
        }
    }

    private fun openRegisterWindow3Activity() {
        val intent = Intent(this, RegisterWindow3Activity::class.java)
        startActivity(intent)
    }

}