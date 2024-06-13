//package com.example.project
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.CalendarView
//import android.widget.ImageButton
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.project.R.id.homeButtonCalendar
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//import java.time.LocalDate
//import java.time.temporal.ChronoUnit
//
//class CalendarActivity : AppCompatActivity(){
//
//    private lateinit var daysToEndOfMenstruaction: TextView
//
//    private lateinit var calendar: CalendarView
//    private lateinit var calendarSettingButton: ImageButton
//    private lateinit var calendarAcountButton: ImageButton
//    private lateinit var homeButtonCalendar: ImageButton
//    private lateinit var userId: String
//    private lateinit var db: FirebaseFirestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.calendar)
//
////        daysToEndOfMenstruaction = findViewById(R.id.daysToEndOfMenstruaction)
//
//        calendar = findViewById(R.id.calendarView)
//        calendarSettingButton = findViewById(R.id.calendarSettingButton)
//        calendarAcountButton = findViewById(R.id.calendarAcountButton)
//        homeButtonCalendar = findViewById(R.id.homeButtonCalendar)
//
//        userId = intent.getStringExtra("USER_ID") ?: ""
//
//        db = FirebaseFirestore.getInstance()
//
//        //dodac otwieranie okna głownego
//
//
//        calendar.setOnDateChangeListener {_, year, month, day ->
//            val date = ("%02d".format(day) + "-" + "%02d".format(month+1) + "%02d".format(year))
//            openDayPeriodActivity(userId)
//        }
//        calendarAcountButton.setOnClickListener {
//            openAccountWindowActivity(userId)
//        }
//
//        calendarSettingButton.setOnClickListener {
//            openSettingsWindowActivity(userId)
//        }
//
//        homeButtonCalendar.setOnClickListener {
//            openHomeWindowActivity(userId)
//        }
//
//    }
//
//
//
//
//
//    private fun openDayPeriodActivity(userId: String) {
//        val intent = Intent(this, DayPeriodActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//    private fun openSettingsWindowActivity(userId: String) {
//        val intent = Intent(this, SettingsWindowActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//
//    private fun openAccountWindowActivity(userId: String){
//        val intent = Intent(this, AccountWindowActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//    private fun openHomeWindowActivity(userId: String){
//        val intent = Intent(this, MainWindowPeriodActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//
//
//
//    }
//
//
package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendar: CalendarView
    private lateinit var calendarSettingButton: ImageButton
    private lateinit var calendarAcountButton: ImageButton
    private lateinit var homeButtonCalendar: ImageButton
    private lateinit var userId: String
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        calendar = findViewById(R.id.calendarView)
        calendarSettingButton = findViewById(R.id.calendarSettingButton)
        calendarAcountButton = findViewById(R.id.calendarAcountButton)
        homeButtonCalendar = findViewById(R.id.homeButtonCalendar)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        calendar.setOnDateChangeListener { _, year, month, day ->
            val selectedDate = LocalDate.of(year, month + 1, day)
            openDayPeriod(userId, selectedDate)
        }

        calendarAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        calendarSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        homeButtonCalendar.setOnClickListener {
            openHomeWindowActivity(userId)
        }
    }

    private fun openDayPeriod(userId: String, date: LocalDate) {
        val intent = Intent(this, DayPeriodActivity::class.java).apply {
            putExtra("USER_ID", userId)
            putExtra("SELECTED_DATE", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
        startActivity(intent)
    }

    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun openAccountWindowActivity(userId: String){
        val intent = Intent(this, AccountWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun openHomeWindowActivity(userId: String){
        val intent = Intent(this, MainWindowPeriodActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }
}
