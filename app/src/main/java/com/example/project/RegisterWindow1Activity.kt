package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegisterWindow1Activity : AppCompatActivity(){

    private lateinit var enterEmailRegister: EditText
    private lateinit var enterPasswordRegister: EditText
    private lateinit var enterPasswordRegisterConfirm: EditText
    private lateinit var enterUsernameRegister: EditText

//    private lateinit var enterLastPeriod: EditText
//    private lateinit var cycleLen: EditText
//    private lateinit var periodLen: EditText
//    private lateinit var medicieInput: EditText
    private lateinit var buttonConfirmRegisterWindow1: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window1)

        // znalezienie elementów

        enterEmailRegister = findViewById(R.id.enterEmailRegister)
        enterPasswordRegister = findViewById(R.id.enterPasswordRegister)
        enterPasswordRegisterConfirm = findViewById(R.id.enterPasswordRegisterConfirm)
        enterUsernameRegister = findViewById(R.id.enterUsernameRegister)
//        enterLastPeriod = findViewById(R.id.enterLastPeriod)
//        cycleLen = findViewById(R.id.cycleLen)
//        periodLen = findViewById(R.id.periodLen)
//        medicieInput = findViewById(R.id.medicineInput)
        buttonConfirmRegisterWindow1 = findViewById(R.id.buttonConfirmRegisterWindow1)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        buttonConfirmRegisterWindow1.setOnClickListener {
            openRegisterWindow2Activity()
        }
    }

    private fun openRegisterWindow2Activity() {
        val intent = Intent(this, RegisterWindow2Activity::class.java)
        startActivity(intent)
    }


}
