package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R.id.homeButtonCalendar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class CalendarActivity : AppCompatActivity(){

    private lateinit var daysToEndOfMenstruaction: TextView
    private lateinit var calendar: CalendarView
    private lateinit var calendarSettingButton: ImageButton
    private lateinit var calendarAcountButton: ImageButton
    private lateinit var homeButtonCalendar: ImageButton
    private lateinit var userId: String
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        daysToEndOfMenstruaction = findViewById(R.id.daysToEndOfMenstruaction)

        calendar = findViewById(R.id.calendarView)
        calendarSettingButton = findViewById(R.id.calendarSettingButton)
        calendarAcountButton = findViewById(R.id.calendarAcountButton)
        homeButtonCalendar = findViewById(R.id.homeButtonCalendar)

        userId = intent.getStringExtra("USER_ID") ?: ""

        db = FirebaseFirestore.getInstance()

        //dodac otwieranie okna głownego


        calendar.setOnDateChangeListener {_, year, month, day ->
            val date = ("%02d".format(day) + "-" + "%02d".format(month+1) + "%02d".format(year))
            openDayPeriodActivity(userId)
        }
        calendarAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        calendarSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

    }
    private fun openDayPeriodActivity(userId: String) {
        val intent = Intent(this, DayPeriodActivity::class.java)
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

//    private fun checkPeriod() {
//        try {
//            val cycleRef = db.collection("users").document(userId).collection("cycles")
//                .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
//                .limit(1)
//                .get()
//                .addOnSuccessListener { documents ->
//                    if (documents.isEmpty) {
//                        Toast.makeText(this, "Brak danych cyklu", Toast.LENGTH_SHORT).show()
//                        return@addOnSuccessListener
//                    }
//
//
//                }
//
//
//        }

    }


