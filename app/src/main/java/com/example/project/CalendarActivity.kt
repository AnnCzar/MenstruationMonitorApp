package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CalendarActivity : AppCompatActivity(){

//    private lateinit var daysToEndOfMenstruaction: EditText
    private lateinit var calendar: CalendarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

//        daysToEndOfMenstruaction = findViewById(R.id.daysToEndOfMenstruaction)
        calendar = findViewById(R.id.calendarView)

        calendar.setOnDateChangeListener {_, year, month, day ->
            val date = ("%02d".format(day) + "-" + "%02d".format(month+1) + "%02d".format(year))
            openDayPeriodActivity()
        }
    }
    private fun openDayPeriodActivity() {
        val intent = Intent(this, DayPeriodActivity::class.java)
        startActivity(intent)
    }

}
