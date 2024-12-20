package com.example.project

import DoctorVisitAdapter
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * The DoctorVisitsActivity class handles the display and management of doctor visits.
 * It allows the user to view, add, edit, delete visits, and receive notifications for upcoming visits.
 */
class DoctorVisitsActivity : AppCompatActivity() {

    private lateinit var visitRV: RecyclerView
    private lateinit var visitAdapter: DoctorVisitAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var homeButtonProfil: ImageButton
    private lateinit var accountWidnowSettingButton: ImageButton
    private lateinit var accountButton: ImageButton
    private lateinit var addVisitButton: Button

    private val doctorsList = mutableListOf<DoctorVisit>()


    /**
     * Initializes the activity and sets up UI components, database connection, and event listeners.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the most recent data.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visits_window)

        db = FirebaseFirestore.getInstance()

        visitRV = findViewById(R.id.visitRV)
        homeButtonProfil = findViewById(R.id.homeButtonProfil)
        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        addVisitButton = findViewById(R.id.addVisitButton)
        accountButton =findViewById(R.id.accountButton)
        visitRV.layoutManager = LinearLayoutManager(this)

        userId = intent.getStringExtra("USER_ID") ?: ""

        visitAdapter = DoctorVisitAdapter(doctorsList, onEditClick = { visit ->
            editVisit(visit)
        }, onDeleteClick = { visit ->
            deleteVisit(visit)
        }, onMapClick = {visit -> showOnMap(visit)})

        visitRV.adapter = visitAdapter

        onResume()

        addVisitButton.setOnClickListener {
            val intent = Intent(this@DoctorVisitsActivity, AddVisitActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
        accountButton.setOnClickListener{
            openAccountWindowActivity(userId)

        }
        accountWidnowSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
        ensureExactAlarmPermission()
        createNotificationChannel()

        homeButtonProfil.setOnClickListener {
            val userRef = db.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { user ->
                    if (user != null) {
                        val statusPregnancy = user.getBoolean("statusPregnancy")
                        if (statusPregnancy != null) {
                            if (!statusPregnancy) {
                                openMainWindowPeriodActivity(userId)
                            } else {
                                openMainWindowPregnancyActivity(userId)
                            }
                        } else {
                        }
                    } else {
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /**
     * Refreshes the list of doctor visits when the activity resumes.
     */
    override fun onResume() {
        super.onResume()
        doctorsList.clear()
        fetchDoctorVisits()
    }

    /**
     * Fetches doctor visits from the Firestore database.
     */
    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctorVisits")
            .whereEqualTo("checked", false)
            .get()
            .addOnSuccessListener { result ->
                doctorsList.clear()
                for (document in result) {
                    val doctor = DoctorVisit(
                        id = document.id,
                        doctorName = document.getString("doctorName") ?: "",
                        visitDate = document.getString("visitDate") ?: "",
                        time = document.getString("time")?.trim() ?: "",
                        isChecked = document.getBoolean("checked") ?: false,
                        extraInfo = document.getString("extraInfo") ?: "",
                        address = document.getString("address") ?: ""
                    )
                    doctorsList.add(doctor)

                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    try {
                        val visitTime = formatter.parse("${doctor.visitDate} ${doctor.time}")?.time
                        if (visitTime != null) {
                            scheduleNotification(visitTime,doctor.id)
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                        Log.d("DoctorVisitsActivity", "Błąd parsowania daty wizyty: ${e.message}")
                    }
                }
                visitAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Edits a specified doctor visit.
     * @param visit The visit to edit.
     */
    private fun editVisit(visit: DoctorVisit) {
        Toast.makeText(this, "Nie ma edycji", Toast.LENGTH_SHORT).show() 
        val intent = Intent(this, ModifyVisitActivity::class.java)
        intent.putExtra("VISIT_ID", visit.id)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    /**
     * Ensures the app has permission to schedule exact alarms.
     */
    private fun ensureExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    /**
     * Schedules a notification for a specific doctor visit.
     * @param visit The doctor visit to schedule a notification for.
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(visitTimeInMillis: Long, visitId: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intencja powiadomienia
        val intent = Intent(this, visitReminder::class.java).apply {
            putExtra("VISIT_ID", visitId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            visitId.hashCode(), // Unikalny identyfikator dla każdego alarmu
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = System.currentTimeMillis()
        val timeUntilVisit = visitTimeInMillis - now

        val alarmTime = when {
            timeUntilVisit > 24 * 60 * 60 * 1000 -> visitTimeInMillis - (24 * 60 * 60 * 1000)
            timeUntilVisit > 60 * 60 * 1000 -> visitTimeInMillis - (60 * 60 * 1000) // 1 godzina wcześniej
            else -> null
        }

        alarmTime?.let {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                it,
                pendingIntent
            )
        }
    }

    /**
     * Creates a notification channel for visit reminders.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "VisitReminderChannel"
            val descriptionText = "Channel for Visits Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("VISIT_REMINDER_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Deletes a specified doctor visit.
     * @param visit The visit to delete.
     */
    private fun deleteVisit(visit: DoctorVisit) {
        db.collection("users").document(userId).collection("doctorVisits")
            .document(visit.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Wizyta została usunięta", Toast.LENGTH_SHORT).show()
                doctorsList.remove(visit)
                visitAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
// NAWIGACJA
    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun showOnMap(visit: DoctorVisit) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("VISIT_ADDRESS", visit.address)
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
}
