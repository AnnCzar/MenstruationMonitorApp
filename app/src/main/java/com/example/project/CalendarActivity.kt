package com.example.project

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R.*
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendar: CompactCalendarView
    private lateinit var calendarSettingButton: ImageButton
    private lateinit var calendarAcountButton: ImageButton
    private lateinit var homeButtonCalendar: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var textView5: TextView
    private lateinit var userId: String
    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(layout.calendar)
        setContentView(R.layout.calendar)

        calendar = findViewById(id.calendarView)
        calendarSettingButton = findViewById(id.calendarSettingButton)
        calendarAcountButton = findViewById(id.calendarAcountButton)
        homeButtonCalendar = findViewById(id.homeButtonCalendar)
        imageView = findViewById(id.imageView)
        imageView2 = findViewById(id.imageView2)
        imageView3 = findViewById(id.imageView3)
        imageView4 = findViewById(id.imageView4)
        textView2 = findViewById(id.textView2)
        textView3 = findViewById(id.textView3)
        textView4 = findViewById(id.textView4)
        textView5 = findViewById(id.textView5)



        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        fetchUserPeriodAndOvulationDates()

        calendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                dateClicked?.let {
                    val selectedDate = dateClicked.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    openDayActivity(userId, selectedDate)
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
            }
        })

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
    override fun onResume() {
        super.onResume()
        checkPregnantStatus()

    }

    private fun checkPregnantStatus() {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    val statusPregnancy = user.getBoolean("statusPregnancy")
                    if (statusPregnancy != null) {
                        if (!statusPregnancy) {
                            textView2.visibility = TextView.VISIBLE
                            textView3.visibility = TextView.VISIBLE
                            textView4.visibility = TextView.VISIBLE
                            textView5.visibility = TextView.VISIBLE
                            imageView.visibility = ImageView.VISIBLE
                            imageView2.visibility = ImageView.VISIBLE
                            imageView3.visibility = ImageView.VISIBLE
                            imageView4.visibility = ImageView.VISIBLE



                        } else {
                            textView2.visibility = TextView.GONE
                            textView3.visibility = TextView.GONE
                            textView4.visibility = TextView.GONE
                            textView5.visibility = TextView.GONE
                            imageView.visibility = ImageView.GONE
                            imageView2.visibility = ImageView.GONE
                            imageView3.visibility = ImageView.GONE
                            imageView4.visibility = ImageView.GONE

                        }
                    }
                }
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchUserPeriodAndOvulationDates() {
        val cyclesRef = db.collection("users").document(userId).collection("cycles")

        cyclesRef.get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty ) {
                    for (document in result) {
                        val startDateString = document.getString("startDate")
                        val endDateString = document.getString("endDate")
                        val ovulationDateString = document.getString("nextOvulationDate")

                        if (startDateString != null && endDateString != null && ovulationDateString != null) {
                            val startDate = LocalDate.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE)
                            val ovulationDate = LocalDate.parse(ovulationDateString, DateTimeFormatter.ISO_LOCAL_DATE)
                            val endDate = LocalDate.parse(endDateString, DateTimeFormatter.ISO_LOCAL_DATE)

                            val periodStart = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                            val periodEnd = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                            val ovulationDay = Date.from(ovulationDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                            val eventDecorator = EventDecorator(calendar)


                            val userRef = db.collection("users").document(userId)
                            userRef.get()
                                .addOnSuccessListener { user ->
                                    if (user != null) {
                                        val statusPregnancy = user.getBoolean("statusPregnancy")
                                        if (statusPregnancy != null) {
                                            if (!statusPregnancy) {                            eventDecorator.markPeriodDays(periodStart, periodEnd)
                                                eventDecorator.markOvulation(ovulationDay)
                                                eventDecorator.markFertilityDays(ovulationDate)}}}}

                        } else {
                            Toast.makeText(this, "Incomplete cycle data for one of the documents.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "No cycle data found.", Toast.LENGTH_SHORT).show()
                }

                fetchDoctorVisits()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching cycle data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        calendar.invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctorVisits")
            .get()
            .addOnSuccessListener { result ->
                Log.d("DoctorVisitsActivity", "Liczba dokumentów: ${result.documents.size}")
                for (document in result) {
                    val visitDateString = document.getString("visitDate")
                    if (visitDateString != null) {
                        val visitDate = LocalDate.parse(visitDateString, DateTimeFormatter.ISO_LOCAL_DATE)

                        val visitDay = Date.from(visitDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                        val eventDecorator = EventDecorator(calendar)
                        eventDecorator.doctorVisits(visitDay)
                    } else {
                        Log.e("DoctorVisitsActivity", "Visit date is missing for document: ${document.id}")
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching doctor visits: ${e.message}", Toast.LENGTH_SHORT).show()
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
                        Intent(this, MainWindowPregnancyActivity::class.java)
                    } else {
                        Intent(this, MainWindowPeriodActivity::class.java)
                    }
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("SELECTED_DATE", date.format(DateTimeFormatter.ISO_DATE))
                    Log.d("dupa", date.format(DateTimeFormatter.ISO_DATE))
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openHomeWindowActivity(userId: String){
        val intent = Intent(this, MainWindowPeriodActivity::class.java).apply {
            putExtra("USER_ID", userId)
            intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        }
        startActivity(intent)
    }
}
