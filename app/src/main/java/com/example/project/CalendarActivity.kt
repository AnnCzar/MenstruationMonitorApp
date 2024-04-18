package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R.id.homeButtonCalendar

class CalendarActivity : AppCompatActivity(){

    private lateinit var daysToEndOfMenstruaction: TextView
    private lateinit var calendar: CalendarView
    private lateinit var calendarSettingButton: ImageButton
    private lateinit var calendarAcountButton: ImageButton
    private lateinit var homeButtonCalendar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        daysToEndOfMenstruaction = findViewById(R.id.daysToEndOfMenstruaction)

        calendar = findViewById(R.id.calendarView)
        calendarSettingButton = findViewById(R.id.calendarSettingButton)
        calendarAcountButton = findViewById(R.id.calendarAcountButton)
        homeButtonCalendar = findViewById(R.id.homeButtonCalendar)

        //dodac otwieranie okna głownego


        calendar.setOnDateChangeListener {_, year, month, day ->
            val date = ("%02d".format(day) + "-" + "%02d".format(month+1) + "%02d".format(year))
            openDayPeriodActivity()
        }
        calendarAcountButton.setOnClickListener {
            openAccountWindowActivity()
        }

        calendarSettingButton.setOnClickListener {
            openSettingsWindowActivity()
        }

    }
    private fun openDayPeriodActivity() {
        val intent = Intent(this, DayPeriodActivity::class.java)
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
