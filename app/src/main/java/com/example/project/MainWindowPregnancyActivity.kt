package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainWindowPregnancyActivity : AppCompatActivity(){
//    private lateinit var daysLeftPregnency: EditText
    private lateinit var medicineCheckbox: CheckBox
    private lateinit var toCalendarButtonPeriod: Button
    private lateinit var begginingPeriodButton: Button
    private lateinit var begginingPregnancyButton: Button
    private lateinit var mainWindowPregnancySettingButton: ImageButton
    private lateinit var mainWindowPregnancyAcountButton: ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_period)

        // znalezienie elementów
//        daysLeftPregnency = findViewById(R.id.daysLeftPregnancy)
        medicineCheckbox = findViewById(R.id.medicineCheckbox)
        toCalendarButtonPeriod = findViewById(R.id.toCalendarButtonPeriod)
        begginingPeriodButton = findViewById(R.id.begginingPeriodButton)
        begginingPregnancyButton = findViewById(R.id.begginingPregnancyButton)
        mainWindowPregnancySettingButton = findViewById(R.id.mainWindowPeriodSettingButton)
        mainWindowPregnancyAcountButton = findViewById(R.id.mainWindowPeriodAcountButton)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        toCalendarButtonPeriod.setOnClickListener {
            openCalendarActivity()
        }
        mainWindowPregnancyAcountButton.setOnClickListener {
            openAccountWindowActivity()
        }

        mainWindowPregnancySettingButton.setOnClickListener {
            openSettingsWindowActivity()
        }

    }
    private fun openCalendarActivity() {
        val intent = Intent(this, CalendarActivity::class.java)
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
