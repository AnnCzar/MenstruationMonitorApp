package com.example.project

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresApi

import java.util.*

class MainWindowPeriodActivity : AppCompatActivity() {
    private lateinit var currentDateTextPeriod: TextView
    private lateinit var daysLeftPeriod: TextView
    private lateinit var daysLeftOwulation: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var endPeriodButton: Button
    private lateinit var toCalendarButtonPeriod: Button
    private lateinit var begginingPeriodButton: Button
    private lateinit var begginingPregnancyButton: Button
    private lateinit var mainWindowPeriodSettingButton: ImageButton
    private lateinit var mainWindowPeriodAcountButton: ImageButton
    private lateinit var logoutButton: Button

    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()

    private var isPeriodStarted = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_period)
        createNotificationChannel()



        userId = intent.getStringExtra("USER_ID") ?: ""

        db = FirebaseFirestore.getInstance()

        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
        medicineRecyclerView.layoutManager = LinearLayoutManager(this)

        medicineAdapter = MedicineAdapter(medicines) { medicine ->
            saveMedicineCheckStatus(medicine)
        }
        medicineRecyclerView.adapter = medicineAdapter

        fetchMedicines()
        fetchLatestCycleData() // Fetch the latest cycle data on activity creation

        currentDateTextPeriod = findViewById(R.id.currentDateTextPeriod)
        daysLeftPeriod = findViewById(R.id.daysLeftPeriod)
        daysLeftOwulation = findViewById(R.id.daysLeftOwulation)
        toCalendarButtonPeriod = findViewById(R.id.toCalendarButtonPeriod)
        begginingPeriodButton = findViewById(R.id.begginingPeriodButton)
        endPeriodButton = findViewById(R.id.endPeriodButton)
        endPeriodButton.visibility = Button.GONE

        begginingPregnancyButton = findViewById(R.id.endingPregnancyButton)

        mainWindowPeriodSettingButton = findViewById(R.id.mainWindowPeriodSettingButton)
        mainWindowPeriodAcountButton = findViewById(R.id.mainWindowPeriodAcountButton)
        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logout()
        }


        toCalendarButtonPeriod.setOnClickListener {
            openCalendarActivity(userId)
        }

        begginingPeriodButton.setOnClickListener {
            addCycleDataToFirestore()
            isPeriodStarted = true
            updateButtonVisibility()
        }

        endPeriodButton.setOnClickListener {
            updateEndDateInFirestore()
            isPeriodStarted = false
            updateButtonVisibility()
        }

        begginingPregnancyButton.setOnClickListener {
            updatePregnancyStatusToTrue(userId)
            openPregnancyBegginingActivity(userId)
        }

        mainWindowPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        mainWindowPeriodSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        // Fetch saved period status
        fetchPeriodStatus()
        currentDateTextPeriod.text = LocalDate.now().toString()
        scheduleNotification()
    }

    private fun updateButtonVisibility() {
        if (isPeriodStarted) {
            begginingPeriodButton.visibility = Button.GONE
            endPeriodButton.visibility = Button.VISIBLE
        } else {
            begginingPeriodButton.visibility = Button.VISIBLE
            endPeriodButton.visibility = Button.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchPeriodStatus() {
        db.collection("users").document(userId).collection("cycles")
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val latestCycle = documents.first()
                    val endDate = latestCycle.getString("endDate")
                    if (endDate.isNullOrEmpty()) {
                        isPeriodStarted = true
                    } else {
                        isPeriodStarted = false
                    }
                    updateButtonVisibility()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Fetch today's medicine status
        fetchTodaysMedicineStatus()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTodaysMedicineStatus() {
        val today = LocalDate.now().toString()
        db.collection("users").document(userId).collection("dailyInfo")
            .document(today).collection("medicines")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val medicineId = document.id
                    val isChecked = document.getBoolean("checked") ?: false
                    medicines.find { it.id == medicineId }?.isChecked = isChecked
                }
                medicineAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addCycleDataToFirestore() {
        val cycleData = HashMap<String, Any>()
        val currentDate = LocalDate.now()

        val nextPeriodDate = calculateNextPeriodDate(currentDate)
        val ovulationDate = calculateOvulationDate(nextPeriodDate)

        cycleData["userId"] = userId
        cycleData["startDate"] = currentDate.toString()
        cycleData["nextPeriodDate"] = nextPeriodDate.toString()
        cycleData["nextOvulationDate"] = ovulationDate.toString()

        db.collection("users").document(userId)
            .collection("cycles").document()
            .set(cycleData)
            .addOnSuccessListener {
                Toast.makeText(this@MainWindowPeriodActivity, "Dane cyklu dodane do Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@MainWindowPeriodActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextPeriodDate(currentDate: LocalDate): LocalDate {
        return currentDate.plusDays(28)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateOvulationDate(nextPeriodDate: LocalDate): LocalDate {
        return nextPeriodDate.minusDays(14)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MedicineReminderChannel"
            val descriptionText = "Channel for Medicine Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MEDICINE_REMINDER_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateEndDateInFirestore() {
        db.collection("users").document(userId).collection("cycles")
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val latestCycle = documents.first()
                    val cycleRef = db.collection("users").document(userId)
                        .collection("cycles").document(latestCycle.id)
                    cycleRef
                        .update("endDate", LocalDate.now().toString())
                        .addOnSuccessListener {
                            Toast.makeText(this@MainWindowPeriodActivity, "Data zakończenia okresu zaktualizowana", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@MainWindowPeriodActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@MainWindowPeriodActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchMedicines() {
        db.collection("users").document(userId).collection("medicines")
            .get()
            .addOnSuccessListener { result ->
                medicines.clear()
                for (document in result) {
                    val medicine = Medicine(
                        id = document.id,
                        name = document.getString("medicineName") ?: "",
                        isChecked = document.getBoolean("isChecked") ?: false
                    )
                    medicines.add(medicine)
                }
                medicineAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchLatestCycleData() {
        db.collection("users").document(userId).collection("cycles")
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Brak danych cyklu", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val latestCycle = documents.first()
                val nextPeriodDate = LocalDate.parse(latestCycle.getString("nextPeriodDate"))
                val nextOvulationDate = LocalDate.parse(latestCycle.getString("nextOvulationDate"))
                val currentDate = LocalDate.now()

                val daysUntilNextPeriod = ChronoUnit.DAYS.between(currentDate, nextPeriodDate)
                val daysUntilNextOvulation = ChronoUnit.DAYS.between(currentDate, nextOvulationDate)

                daysLeftPeriod.text = daysUntilNextPeriod.toString()
                daysLeftOwulation.text = daysUntilNextOvulation.toString()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMedicineCheckStatus(medicine: Medicine) {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(LocalDate.now().toString())
            .collection("medicines")
            .document(medicine.id)
            .set(mapOf("checked" to medicine.isChecked))
            .addOnSuccessListener {
                Toast.makeText(this, "Medicine status updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updatePregnancyStatusToTrue(userId: String) {
        val userRef = db.collection("users").document(userId)
        userRef
            .update("statusPregnancy", true)
            .addOnSuccessListener {
                Toast.makeText(this@MainWindowPeriodActivity, "Status ciąży zaktualizowany na true", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@MainWindowPeriodActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


private fun scheduleNotification() {
    val intent = Intent(this, MedicineReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 9) // Set the time you want the reminder
    }

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

    private fun logout() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("USER_ID")
        editor.apply()

        val intent = Intent(this, LoginWindowActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openPregnancyBegginingActivity(userId: String) {
        val intent = Intent(this, PregnancyBegginingActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openCalendarActivity(userId: String) {
        val intent = Intent(this, CalendarActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openAccountWindowActivity(userId: String) {
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
