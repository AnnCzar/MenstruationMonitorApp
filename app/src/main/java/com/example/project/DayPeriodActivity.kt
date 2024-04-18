package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DayPeriodActivity : AppCompatActivity(){

    private lateinit var cycleDayPeriod: TextView
//    private lateinit var medicineCheckboxPeriodDay: EditText
    private lateinit var doctorsListPeriod: ListView
    private lateinit var additionalInfoPeriod: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonPlus: Button
    private lateinit var dayPeriodSettingsButton: ImageButton
    private lateinit var dayPeriodAcountButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)
//
        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
//        medicineCheckboxPeriodDay = findViewById(R.id.medicineCheckboxPeriodDay)
        doctorsListPeriod = findViewById(R.id.doctorsListPeriod)
        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
        buttonMinus = findViewById(R.id.buttonMinus)
        buttonPlus = findViewById(R.id.buttonPlus)
        dayPeriodSettingsButton = findViewById(R.id.dayPeriodSettingsButton)
        dayPeriodAcountButton = findViewById(R.id.dayPeriodAcountButton)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        additionalInfoPeriod.setOnClickListener {
            openAdditionalInformationActivity()
        }

        dayPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity()
        }

        dayPeriodSettingsButton.setOnClickListener {
            openSettingsWindowActivity()
        }
        buttonMinus.setOnClickListener {

        }
        buttonPlus.setOnClickListener {

        }
    }


    private fun openAdditionalInformationActivity() {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        startActivity(intent)
    }
    private fun openSettingsWindowActivity() {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        startActivity(intent)
    }

    private fun openAccountWindowActivity(){
        val intent = Intent(this, AccountWindowActivity::class.java)
        startActivity(intent)
    }


}
