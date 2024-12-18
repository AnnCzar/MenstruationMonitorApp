package com.example.project

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.graphics.Color

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.MainWindowPeriodActivity.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import database.collections.Pregnancy
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class MainWindowPregnancyActivity : AppCompatActivity() {
    private lateinit var daysLeftPregnancy: TextView

    private lateinit var toCalendarButtonPregn: ImageButton
    private lateinit var mainWindowPregnancySettingButton: ImageButton
    private lateinit var mainWindowPregnancyAccountButton: ImageButton
     private lateinit var dateTextPregnancy: TextView
    private lateinit var endingPregnancyButton: Button

    private lateinit var additionalInfoPregnancy: Button

    private lateinit var selectedDate: LocalDate
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String



    private lateinit var doctorsRecyclerViewPregnancy: RecyclerView
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



    private lateinit var medicinePregnancy: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()


    companion object {
        private const val TAG = "MainWindowPregnancy"
        private const val PREGNANCY_DURATION_DAYS = 266L
    }




    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_pregnancy)


        userId = intent.getStringExtra("USER_ID") ?: ""

        db = FirebaseFirestore.getInstance()
        checkDate()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@MainWindowPregnancyActivity, "Cofanie jest wyłączone!", Toast.LENGTH_SHORT).show()
            }
        })




        medicinePregnancy = findViewById(R.id.medicinePregnancy)
        medicinePregnancy.layoutManager = LinearLayoutManager(this)
        medicineAdapter = MedicineAdapter(medicines) { medicine ->
            saveMedicineCheckStatus(medicine,selectedDate)
        }
        medicinePregnancy.adapter = medicineAdapter


        toCalendarButtonPregn = findViewById(R.id.toCalendarButtonPregn)
        mainWindowPregnancySettingButton = findViewById(R.id.mainWindowPregnancySettingButton)
        mainWindowPregnancyAccountButton = findViewById(R.id.mainWindowPregnancyAcountButton)
        dateTextPregnancy = findViewById(R.id.dateTextPregnancy)
        daysLeftPregnancy = findViewById(R.id.daysLeftPregnancy)


        endingPregnancyButton = findViewById(R.id.endingPregnancyButton)
        doctorsRecyclerViewPregnancy = findViewById(R.id.doctorsRecyclerViewPregnancy)
        additionalInfoPregnancy = findViewById(R.id.additionalInfoPregnancy)
        doctorsRecyclerViewPregnancy.layoutManager = LinearLayoutManager(this)

        doctorAdapter = DoctorVisitAdapter(doctors)
        doctorsRecyclerViewPregnancy.adapter = doctorAdapter



        dateTextPregnancy.text = selectedDate.toString()
        fetchMedicines()
//        fetchMedicinesStatus(selectedDate)
        fetchDoctorVisits()
        fetchPregnancyData()



        toCalendarButtonPregn.setOnClickListener {
            openCalendarActivity()
        }

        mainWindowPregnancyAccountButton.setOnClickListener {
            openAccountWindowActivity()
        }

        mainWindowPregnancySettingButton.setOnClickListener {
            openSettingsWindowActivity()
        }

        endingPregnancyButton.setOnClickListener {
            updatePregnancyStatusToFalse(userId)
            openMainPeriodActivity(userId)
        }
        additionalInfoPregnancy.setOnClickListener {
            openAdditionalInformationActivity(userId, selectedDate)
        }
        FirebaseMessaging.getInstance().subscribeToTopic("user_${userId}")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("FCM", msg)
            }
        db.collection("Chats")
            .whereEqualTo("receiver", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }
                // Znajdź najnowszą wiadomość (na podstawie timestamp)
                val latestMessage = snapshots?.documents
                    ?.map { it.toObject(Message::class.java) }
                    ?.maxByOrNull { it?.timestamp ?: 0L }
                if (latestMessage != null) {
                    sendNotification(latestMessage)
                }
            }


        FirebaseMessaging.getInstance().subscribeToTopic("user_${userId}")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) "Subscribed" else "Subscription failed"
                Log.d("FCM", msg)
            }

        db.collection("Chats") // Poprawiona nazwa kolekcji
            .whereEqualTo("receiver", userId) // Użycie poprawnej nazwy pola "receiver"
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }

                // Znalezienie najnowszej wiadomości na podstawie pola "timestamp"
                val latestMessage = snapshots?.documents
                    ?.mapNotNull { it.toObject(Message::class.java) } // Konwersja do obiektu Message
                    ?.maxByOrNull { it.timestamp ?: 0L }

                if (latestMessage != null) {
                    sendNotification(latestMessage)
                }
            }}

    // Klasa Message odpowiadająca strukturze dokumentu w Firestore
    data class Message(
        val message: String = "",
        val receiver: String = "",
        val sender: String = "",
        val timestamp: Long = 0L,
        val isseen: Boolean = false
    )


    private fun sendNotification(message: MainWindowPregnancyActivity.Message) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(message.sender)
            .get()
            .addOnSuccessListener { document ->
                val senderLogin = document.getString("login") ?: "Nieznany użytkownik"

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "default",
                        "Default Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(this, "default")
                    .setContentTitle("Nowa wiadomość od $senderLogin")
                    .setContentText(message.message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(message.timestamp.toInt(), notification)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        checkDate()
        fetchMedicines()
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
    private fun fetchPregnancyData() {
        db.collection("users").document(userId).collection("pregnancies")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val pregnancy = document.toObject(Pregnancy::class.java)
                    pregnancy.startDatePregnancy?.let { startDate ->
                        val endDate = calculateEndDate(startDate)
                        val daysLeft = calculateDaysLeft(endDate)
                        daysLeftPregnancy.text = daysLeft.toString()
                        Log.d(TAG, "Days left until due date: $daysLeft")

                        return@addOnSuccessListener
                    }
                }
                daysLeftPregnancy.text = "N/A"
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting pregnancy documents: ", exception)
                daysLeftPregnancy.text = "Error"
            }
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
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateEndDate(startDate: Date): LocalDate {
        val startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return startLocalDate.plusDays(PREGNANCY_DURATION_DAYS)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDaysLeft(endDate: LocalDate): Long {
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(today, endDate)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchMedicinesStatus(selectedDate: LocalDate) {


        val dailyInfoRef = db.collection("users").document(userId)
            .collection("dailyInfo").document(selectedDate.toString())
            .collection("medicines")
        Log.d("medicinesBeforeFetch dupa", medicines.toString())
        dailyInfoRef.get().addOnSuccessListener { documents ->
            if (medicines.isNotEmpty()) {
                medicines.forEach { it.isChecked = false }
            for (document in documents) {

                val medicineId = document.id
                val isChecked = document.getBoolean("checked") ?: false
                Log.d("FetchedMedicineId", medicineId)
                val medicine = medicines.find { it.id == medicineId }
                if (medicine != null) {
                    medicine.isChecked = isChecked
                } else {
                    Log.d("MedicineNotFound", "No medicine found with id: $medicineId")
                }

            }
            runOnUiThread {
                Log.d(
                    "medicinesAfterUpdate",
                    medicines.toString()
                )  // Log the medicines list after updating
                medicineAdapter.notifyDataSetChanged()
            }

        } else {
        Log.d("medicinesEmpty", "Medicines list is empty!")
    }


        }.addOnFailureListener { e ->
            Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("FirestoreError", "Błąd przy pobieraniu leków: ${e.message}")
        }
       }

    @RequiresApi(Build.VERSION_CODES.O)
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

                // Po zakończeniu pobierania leków wywołaj fetchMedicinesStatus()
                fetchMedicinesStatus(selectedDate) // Teraz fetchMedicinesStatus() będzie wywołane po fetchMedicines
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd przy pobieraniu leków: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }


//    private fun fetchMedicines() {
//        db.collection("users").document(userId).collection("medicines")
//            .get()
//            .addOnSuccessListener { result ->
//                medicines.clear()
//                for (document in result) {
//                    val medicine = Medicine(
//                        id = document.id,
//                        name = document.getString("medicineName") ?: "",
//                        isChecked = document.getBoolean("isChecked") ?: false
//                    )
//                    medicines.add(medicine)
//                }
//                medicineAdapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Error fetching medicines: ", e)
//            }
//    }

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
    private fun updatePregnancyStatusToFalse(userId: String) {
        val userRef = db.collection("users").document(userId)
        userRef
            .update("statusPregnancy", false)
            .addOnSuccessListener {
                Toast.makeText(this@MainWindowPregnancyActivity, "Status ciąży zaktualizowany na false", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@MainWindowPregnancyActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }

    private fun openCalendarActivity() {
        val intent = Intent(this, CalendarActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openMainPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openSettingsWindowActivity() {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAccountWindowActivity() {
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAdditionalInformationActivity(userId: String, date: LocalDate) {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        startActivity(intent)
    }
}
