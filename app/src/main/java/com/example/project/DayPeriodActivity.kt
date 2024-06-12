package com.example.project

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class DayPeriodActivity : AppCompatActivity(){

    private lateinit var cycleDayPeriod: TextView
    private lateinit var  imageButtonDayPeriod: ImageButton
//    private lateinit var medicineCheckboxPeriodDay: EditText
    private lateinit var doctorsListPeriod: ListView
    private lateinit var additionalInfoPeriod: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonPlus: Button
    private lateinit var dayPeriodSettingsButton: ImageButton
    private lateinit var dayPeriodAcountButton: ImageButton

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)
        imageButtonDayPeriod = findViewById(R.id.imageButtonDayPeriod)
//
        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
//        medicineCheckboxPeriodDay = findViewById(R.id.medicineCheckboxPeriodDay)
        doctorsListPeriod = findViewById(R.id.doctorsListPeriod)
        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
        buttonMinus = findViewById(R.id.buttonMinus)
        buttonPlus = findViewById(R.id.buttonPlus)
        dayPeriodSettingsButton = findViewById(R.id.dayPeriodSettingsButton)
        dayPeriodAcountButton = findViewById(R.id.dayPeriodAcountButton)


        userId = intent.getStringExtra("USER_ID") ?: ""

        db = FirebaseFirestore.getInstance()

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        additionalInfoPeriod.setOnClickListener {
            openAdditionalInformationActivity(userId)
        }

        dayPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        dayPeriodSettingsButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
        buttonMinus.setOnClickListener {

        }
        buttonPlus.setOnClickListener {

        }
        // dodac przejscie do okna glownego
    }


    private fun openAdditionalInformationActivity(userId: String) {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openAccountWindowActivity(userId: String){
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }


}
