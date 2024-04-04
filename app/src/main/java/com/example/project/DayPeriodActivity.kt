package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DayPeriodActivity : AppCompatActivity(){

//    private lateinit var cycleDayPeriod: EditText
//    private lateinit var medicineCheckboxPeriodDay: EditText
    private lateinit var doctorsListPeriod: ListView
    private lateinit var additionalInfoPeriod: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonPlus: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)
//
//        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
//        medicineCheckboxPeriodDay = findViewById(R.id.medicineCheckboxPeriodDay)
        doctorsListPeriod = findViewById(R.id.doctorsListPeriod)
        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
        buttonMinus = findViewById(R.id.buttonMinus)
        buttonPlus = findViewById(R.id.buttonPlus)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        additionalInfoPeriod.setOnClickListener {
            AdditionalInformationActivity()
        }
    }
    private fun openAdditionalInformationActivity() {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        startActivity(intent)
    }


}
