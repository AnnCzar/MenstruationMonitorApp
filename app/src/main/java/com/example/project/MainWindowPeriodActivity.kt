package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainWindowPeriodActivity : AppCompatActivity(){
    private lateinit var currentDateTextPregnancy: TextView
    private lateinit var daysLeftPeriod: TextView
    private lateinit var daysLeftOwulation: TextView
//    private lateinit var medicineCheckbox: CheckBox
    private lateinit var toCalendarButtonPeriod: Button
    private lateinit var begginingPeriodButton: Button
    private lateinit var begginingPregnancyButton: Button
    private lateinit var mainWindowPeriodSettingButton: ImageButton
    private lateinit var mainWindowPeriodAcountButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_period)

        // Initialize views
        currentDateTextPregnancy = findViewById(R.id.currentDateTextPregnancy)
        daysLeftPeriod = findViewById(R.id.daysLeftPeriod)
        daysLeftOwulation = findViewById(R.id.daysLeftOwulation)
//        medicineCheckbox = findViewById(R.id.medicineCheckbox)
        toCalendarButtonPeriod = findViewById(R.id.toCalendarButtonPeriod)
        begginingPeriodButton = findViewById(R.id.begginingPeriodButton)
        begginingPregnancyButton = findViewById(R.id.begginingPregnancyButton)

        mainWindowPeriodSettingButton = findViewById(R.id.mainWindowPeriodSettingButton)
        mainWindowPeriodAcountButton = findViewById(R.id.mainWindowPeriodAcountButton)


        toCalendarButtonPeriod.setOnClickListener {
            openCalendarActivity()
        }

        //!!!
        begginingPeriodButton.setOnClickListener{

        }
        // open a window where you need to enter information about the beginning of pregnancy
        begginingPregnancyButton.setOnClickListener {

        }
        mainWindowPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity()
        }

        mainWindowPeriodSettingButton.setOnClickListener {
            openSettingsWindowActivity()
        }
        begginingPregnancyButton.setOnClickListener {
            openPregnancyBegginingActivity()
        }


    }


    private fun openPregnancyBegginingActivity() {
        val intent = Intent(this, PregnancyBegginingActivity::class.java)
        startActivity(intent)
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

    private fun openPregnancyBegginingAcivity(){
        val intent = Intent(this, PregnancyBegginingActivity::class.java)
        startActivity(intent)

    }

}
