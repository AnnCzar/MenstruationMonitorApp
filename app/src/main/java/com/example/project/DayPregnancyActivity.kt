package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DayPregnancyActivity : AppCompatActivity(){
    private lateinit var cycleDayPregnancy: TextView
//    private lateinit var medicineCheckboxPregnancyDay: EditText
    private lateinit var doctorsListPregnancy: ListView
    private lateinit var additionalInfoPregnancy: Button
    private lateinit var dayPregnancySettingButton: ImageButton
    private lateinit var dayPregnancyAcountButton: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)
//
        cycleDayPregnancy = findViewById(R.id.cycleDayPregnancy)
//        medicineCheckboxPregnancyDay = findViewById(R.id.medicineCheckboxPregnancyDay)
        doctorsListPregnancy = findViewById(R.id.doctorsListPregnancy)
        additionalInfoPregnancy = findViewById(R.id.additionalInfoPregnancy)
        dayPregnancySettingButton = findViewById(R.id.dayPregnancySettingButton)
        dayPregnancyAcountButton = findViewById(R.id.dayPregnancyAcountButton)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        additionalInfoPregnancy.setOnClickListener {
            AdditionalInformationActivity()
        }
        dayPregnancySettingButton.setOnClickListener {
            openSettingsWindowActivity()
        }

        dayPregnancyAcountButton.setOnClickListener {
            openAccountWindowActivity()
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
