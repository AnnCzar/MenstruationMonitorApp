package com.example.project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.O)
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
            openDayActivity(userId, selectedDate)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openDayActivity(userId: String, date: LocalDate) {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val pregnancyStatus = document.getBoolean("statusPregnancy") ?: false
                    val intent = if (pregnancyStatus) {
                        Intent(this, DayPregnancyActivity::class.java)
                    } else {
                        Intent(this, DayPeriodActivity::class.java)
                    }
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("SELECTED_DATE", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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
