//package com.example.project
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.CheckBox
//import android.widget.EditText
//import androidx.appcompat.app.AppCompatActivity
//
//class MainWindowPeriodActivity : AppCompatActivity(){
//    private lateinit var daysLeftPeriod:EditText
//    private lateinit var daysLeftOwulation:EditText
//    private lateinit var medicineCheckbox:CheckBox
//    private lateinit var toCalendarButtonPeriod:Button
//    private lateinit var begginingPeriodButton:Button
//    private lateinit var begginingPregnancyButton:Button
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.main_window_period)
//
//        // znalezienie elementów
//        daysLeftPeriod = findViewById(R.id.daysLeftPeriod)
//        daysLeftOwulation = findViewById(R.id.daysLeftOwulation)
//        medicineCheckbox = findViewById(R.id.medicineCheckbox)
//        toCalendarButtonPeriod = findViewById(R.id.toCalendarButtonPeriod)
//        begginingPeriodButton = findViewById(R.id.begginingPeriodButton)
//        begginingPregnancyButton = findViewById(R.id.begginingPregnancyButton)
//
//        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
//        toCalendarButtonPeriod.setOnClickListener {
//            openCalendarActivity()
//        }
//
//    }
//    private fun openCalendarActivity() {
//        val intent = Intent(this, CalendarActivity::class.java)
//        startActivity(intent)
//    }
//
//}
package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainWindowPeriodActivity : AppCompatActivity(){
//    private lateinit var daysLeftPeriod: EditText
//    private lateinit var daysLeftOwulation: EditText
//    private lateinit var medicineCheckbox: CheckBox
    private lateinit var toCalendarButtonPeriod: Button
    private lateinit var begginingPeriodButton: Button
    private lateinit var begginingPregnancyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_period)

        // Initialize views
//        daysLeftPeriod = findViewById(R.id.daysLeftPeriod)
//        daysLeftOwulation = findViewById(R.id.daysLeftOwulation)
//        medicineCheckbox = findViewById(R.id.medicineCheckbox)
        toCalendarButtonPeriod = findViewById(R.id.toCalendarButtonPeriod)
        begginingPeriodButton = findViewById(R.id.begginingPeriodButton)
        begginingPregnancyButton = findViewById(R.id.begginingPregnancyButton)

        // Listen for button click
        toCalendarButtonPeriod.setOnClickListener {
            openCalendarActivity()
        }
    }

    private fun openCalendarActivity() {
        val intent = Intent(this, CalendarActivity::class.java)
        startActivity(intent)
    }
}
