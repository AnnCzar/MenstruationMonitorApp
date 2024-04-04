package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DayPregnancyActivity : AppCompatActivity(){
//    private lateinit var cycleDayPregnancy: EditText
//    private lateinit var medicineCheckboxPregnancyDay: EditText
    private lateinit var doctorsListPregnancy: ListView
    private lateinit var additionalInfoPregnancy: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)
//
//        cycleDayPregnancy = findViewById(R.id.cycleDayPregnancy)
//        medicineCheckboxPregnancyDay = findViewById(R.id.medicineCheckboxPregnancyDay)
        doctorsListPregnancy = findViewById(R.id.doctorsListPregnancy)
        additionalInfoPregnancy = findViewById(R.id.additionalInfoPregnancy)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        additionalInfoPregnancy.setOnClickListener {
            AdditionalInformationActivity()
        }
    }
    private fun openAdditionalInformationActivity() {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        startActivity(intent)
    }

}
