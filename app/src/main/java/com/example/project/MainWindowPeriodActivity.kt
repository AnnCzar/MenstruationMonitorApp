package com.example.project

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import android.graphics.Color



import java.util.*

class MainWindowPeriodActivity : AppCompatActivity() {
    private lateinit var currentDateTextPeriod: TextView
    private lateinit var daysLeftPeriod: TextView
    private lateinit var daysLeftOwulation: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var endPeriodButton: Button
    private lateinit var toCalendarButtonPeriod: ImageButton
    private lateinit var begginingPeriodButton: Button
    private lateinit var mainWindowPeriodSettingButton: ImageButton
    private lateinit var mainWindowPeriodAcountButton: ImageButton
    private lateinit var selectedDate: LocalDate
    private lateinit var additionalInfoPeriod: Button
    private lateinit var cycleDayPeriod: TextView

    private lateinit var doctorRecyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorVisitAdapter
    class DoctorVisitAdapter(
        private val visits: List<DoctorVisit>,
    ) : RecyclerView.Adapter<DoctorVisitAdapter.DoctorVisitViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return DoctorVisitViewHolder(view)
        }

        override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
            val visit = visits[position]
            holder.bind(visit)
        }

        override fun getItemCount(): Int = visits.size

        class DoctorVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(android.R.id.text1)

            fun bind(visit: DoctorVisit) {

                textView.text = "${visit.doctorName} - Godzina: ${visit.time}\nInformacje: ${visit.extraInfo}"
                textView.setTextColor(Color.BLACK)

            }
        }
    }
    private val doctors = mutableListOf<DoctorVisit>()

    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_period)
        createNotificationChannel()

        userId = intent.getStringExtra("USER_ID") ?: ""

        db = FirebaseFirestore.getInstance()

        checkDate()
//        fetchTodaysCycleDay()
//        fetchUserCycleLength()
        fetchPeriodStatus()



        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
        medicineRecyclerView.layoutManager = LinearLayoutManager(this)
        medicineAdapter = MedicineAdapter(medicines) { medicine ->
            saveMedicineCheckStatus(medicine, selectedDate)
        }
        medicineRecyclerView.adapter = medicineAdapter

        doctorRecyclerView = findViewById(R.id.doctorsRecyclerView)
        doctorRecyclerView.layoutManager = LinearLayoutManager(this)
        currentDateTextPeriod = findViewById(R.id.currentDateTextPeriod)
        daysLeftPeriod = findViewById(R.id.daysLeftPeriod)
        daysLeftOwulation = findViewById(R.id.daysLeftOwulation)
        toCalendarButtonPeriod = findViewById(R.id.toCalendarButtonPeriod)
        begginingPeriodButton = findViewById(R.id.begginingPeriodButton)
        endPeriodButton = findViewById(R.id.endPeriodButton)
        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
        mainWindowPeriodSettingButton = findViewById(R.id.mainWindowPeriodSettingButton)
        mainWindowPeriodAcountButton = findViewById(R.id.mainWindowPeriodAcountButton)

        currentDateTextPeriod.text = selectedDate.toString()

        doctorAdapter = DoctorVisitAdapter(doctors)
        doctorRecyclerView.adapter = doctorAdapter

        fetchMedicinesStatus(selectedDate)
        fetchLatestCycleData()
        fetchDoctorVisits()


        endPeriodButton.visibility = Button.GONE
        begginingPeriodButton.visibility = Button.GONE



        toCalendarButtonPeriod.setOnClickListener {
            openCalendarActivity(userId)
        }
        additionalInfoPeriod.setOnClickListener {
            openAdditionalInformationActivity(userId, selectedDate)
        }


        begginingPeriodButton.setOnClickListener {
            openPeriodBeggining(userId)
            updateButtonVisibility(true)
        }
        endPeriodButton.setOnClickListener {
            openPeriodEnding(userId)
            updateButtonVisibility(false)

        }

        mainWindowPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        mainWindowPeriodSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        fetchMedicines()
        fetchTodaysCycleDay()

        scheduleNotification()
    }



//    private fun checkPeriod(){
//        val userRef = db.collection("users").document(userId)
//        userRef.collection("cycles").get()
//            .addOnSuccessListener { querySnapshot ->
//                val hasCycles = !querySnapshot.isEmpty
//                if (!hasCycles) {
//                    begginingPeriodButton.visibility = Button.VISIBLE
//                } else {
//                    begginingPeriodButton.visibility = Button.GONE
//                }
//
//            }
//            .addOnFailureListener { e ->
//                println("Error fetching cycles collection: $e")
//            }
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        checkDate()
        fetchMedicinesStatus(selectedDate)
        fetchPeriodStatus()
        fetchLatestCycleData()
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
                    if (latestCycle.contains("endDate")) {
                        updateButtonVisibility(false)
                    } else {

                        updateButtonVisibility(true)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDate() {
        val dateString = intent?.getStringExtra("SELECTED_DATE")
        selectedDate = if (dateString.isNullOrEmpty()) {
            LocalDate.now()
        } else {
            LocalDate.parse(dateString)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTodaysCycleDay() {   // to jest uzywane do przedstawiania ile dni do nastepengo okresu

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val cycleLength = userDoc.getLong("cycleLength")?.toLong()

                if (cycleLength != null) {
                    db.collection("users").document(userId).collection("cycles")
                        .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val latestCycle = querySnapshot.documents[0]
                                val lastStartDateString = latestCycle.getString("startDate")

                                if (lastStartDateString != null) {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val lastStartDate: Date = sdf.parse(lastStartDateString)!!
                                    val lastStartLocalDate = lastStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                                    val currentDate = LocalDate.now()

                                    val daysSinceLastStart = ChronoUnit.DAYS.between(lastStartLocalDate, selectedDate).toInt()
                                    val currentCycleDay = (daysSinceLastStart % cycleLength) + 1

                                    val nextMenstruationDate = lastStartLocalDate.plusDays(cycleLength)
                                    val daysUntilNextMenstruation = ChronoUnit.DAYS.between(selectedDate, nextMenstruationDate).toInt()

                                    displayCycleDay(currentCycleDay)
                                    runOnUiThread {
                                        daysLeftPeriod.text = daysUntilNextMenstruation.toString()
                                    }
                                } else {
                                    Log.e("CycleDataError", "Last start date not found.")
                                }
                            } else {
                                Log.e("CycleDataError", "No cycles found.")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Failed to fetch cycles: $e")
                        }
                } else {
                    Log.e("UserDataError", "Cycle length not found for user.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to fetch user data: $e")
            }
    }




    private fun updateButtonVisibility(isPeriodStarted: Boolean) {
        if(!isPeriodStarted){
            begginingPeriodButton.visibility = Button.VISIBLE
            endPeriodButton.visibility = Button.GONE
        }
        else if(isPeriodStarted) {
            begginingPeriodButton.visibility = Button.GONE
            endPeriodButton.visibility = Button.VISIBLE
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayCycleDay(cycleDay: Long) {
        Log.d("UI", "Updating UI with cycleDay: $cycleDay")
        runOnUiThread {
            cycleDayPeriod.text = "$cycleDay"
        }
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
    private fun fetchMedicines() {
        db.collection("users").document(userId).collection("medicines")
            .get()
            .addOnSuccessListener { documents ->
                medicines.clear()
                for (document in documents) {
                    val medicineId = document.id
                    val medicineName = document.getString("medicineName") ?: "Brak nazwy"
                    val dose = document.getString("doseMedicine") ?: "Brak dawki"
                    val time = document.getString("timeMedicine") ?: "Brak czasu"
                    val isChecked = document.getBoolean("checked") ?: false

                    medicines.add(Medicine(medicineId, medicineName, isChecked, dose, time))
                }
                Log.d("FirestoreData", "Pobrano leki: $medicines")
                medicineAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching medicines: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchLatestCycleData() {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val lastPeriodDateValue = userDoc.get("lastPeriodDate")
                    val cycleLength = userDoc.getLong("cycleLength")?.toInt()
                    val periodLength = userDoc.getLong("periodLength")?.toInt()

                    if (cycleLength == null || periodLength == null) {
                        showToast("Brak pełnych danych użytkownika")
                        return@addOnSuccessListener
                    }

                    val lastPeriodDate = parseCustomDate(lastPeriodDateValue)

                    if (lastPeriodDate == null) {
                        showToast("Nieprawidłowy format daty: $lastPeriodDateValue")
                        return@addOnSuccessListener
                    }

                    fetchLatestCycleDocument(lastPeriodDate, cycleLength)
                } else {
                    showToast("Nie znaleziono danych użytkownika")
                }
            }
            .addOnFailureListener { e ->
                showToast("Błąd: ${e.message}")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseCustomDate(dateValue: Any?): LocalDate? {
        return try {
            if (dateValue is Map<*, *>) {
                val year = (dateValue["year"] as? Long)?.toInt() ?: return null
                val month = (dateValue["monthValue"] as? Long)?.toInt() ?: return null
                val day = (dateValue["dayOfMonth"] as? Long)?.toInt() ?: return null
                LocalDate.of(year, month, day)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)  // to uzywane ile dni do menstruacji
    private fun fetchLatestCycleDocument(lastPeriodDate: LocalDate, cycleLength: Int) {
        db.collection("users").document(userId).collection("cycles")
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    calculateAndDisplayDates(lastPeriodDate, cycleLength)
                } else {
                    val latestCycle = documents.first()
                    val nextOvulationDate = LocalDate.parse(latestCycle.getString("nextOvulationDate"))
                    displayDates(nextOvulationDate, cycleLength)
                }
            }
            .addOnFailureListener { e ->
                showToast("Błąd: ${e.message}")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAndDisplayDates(lastPeriodDate: LocalDate, cycleLength: Int) {
        val today = LocalDate.now()

        val daysSinceLastPeriod = ChronoUnit.DAYS.between(lastPeriodDate, selectedDate)
        val currentCycleDay = (daysSinceLastPeriod % cycleLength).toInt() + 1

        val daysUntilNextPeriod = if (daysSinceLastPeriod < cycleLength) {
            cycleLength - daysSinceLastPeriod
        } else {
            cycleLength - (daysSinceLastPeriod % cycleLength)
        }

        // do poprawy
        val nextOvulationDate = lastPeriodDate.plusDays((cycleLength / 2).toLong())


        runOnUiThread {
            daysLeftOwulation.text = ChronoUnit.DAYS.between(selectedDate, nextOvulationDate).toString()
//            daysLeftPeriod .text = daysUntilNextPeriod.toString()
//            cycleDayPeriod.text = currentCycleDay.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)  // to jest do wyswietlaniaa ile dni do owulacji
    private fun displayDates(nextOvulationDate: LocalDate, cycleLength: Int) {
        val today = LocalDate.now()

        var daysUntilNextOvulation = ChronoUnit.DAYS.between(selectedDate, nextOvulationDate)

        if (daysUntilNextOvulation < 0) {
            val adjustedOvulationDate = nextOvulationDate.plusDays(cycleLength.toLong())
            daysUntilNextOvulation = ChronoUnit.DAYS.between(selectedDate
                , adjustedOvulationDate)
        }

        runOnUiThread {
            daysLeftOwulation.text = daysUntilNextOvulation.toString()
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchMedicinesStatus(selectedDate: LocalDate) {

        val dailyInfoRef = db.collection("users").document(userId)
            .collection("dailyInfo").document(selectedDate.toString())
            .collection("medicines")

        dailyInfoRef.get().addOnSuccessListener { documents ->

//            medicines.forEach { it.isChecked = false }

            for (document in documents) {
                val medicineId = document.id
                val isChecked = document.getBoolean("checked") ?: false
                medicines.find { it.id == medicineId }?.isChecked = isChecked

            }
            runOnUiThread { medicineAdapter.notifyDataSetChanged() }


        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMedicineCheckStatus(medicine: Medicine, selectedDate: LocalDate) {
        Log.d("Zapis daty", selectedDate.toString())

        val dateKey = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dailyInfoRef = db.collection("users").document(userId)
            .collection("dailyInfo").document(dateKey)
            .collection("medicines").document(medicine.id)

        dailyInfoRef.set(mapOf("checked" to medicine.isChecked))
            .addOnSuccessListener {

                medicines.find { it.id == medicine.id }?.isChecked = medicine.isChecked
                runOnUiThread { medicineAdapter.notifyDataSetChanged() }

                Toast.makeText(this, "Status leku zaktualizowany dla daty $dateKey", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
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
        set(Calendar.HOUR_OF_DAY, 9)
    }

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

    

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctorVisits")
            .whereEqualTo("visitDate", selectedDate.toString()) // Dodano warunek filtrowania po dacie
            .get()
            .addOnSuccessListener { result ->
                Log.d("DoctorVisitsActivity", "Liczba dokumentów: ${result.documents.size}")
                doctors.clear()
                for (document in result) {
                    val doctor = DoctorVisit(
                        id = document.id,
                        doctorName = document.getString("doctorName") ?: "",
                        visitDate = document.getString("visitDate") ?: "",
                        time = document.getString("time") ?: "",
                        isChecked = document.getBoolean("checked") ?: false,
                        extraInfo = document.getString("extraInfo") ?: "",
                        address = document.getString("address") ?: ""
                    )
                    doctors.add(doctor)
                }
                doctorAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
    private fun openPeriodEnding(userId: String){
        val intent = Intent(this, PeriodEndingActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }


    private fun openPeriodBeggining(userId: String) {
        val intent = Intent(this, PeriodBegginingActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAdditionalInformationActivity(userId: String, date: LocalDate) {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        startActivity(intent)
    }

}
