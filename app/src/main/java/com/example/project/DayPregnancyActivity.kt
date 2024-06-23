package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import database.collections.Pregnancy
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

class DayPregnancyActivity : AppCompatActivity() {

    private lateinit var dateDayPregnancy: TextView
    private lateinit var cycleDayPregnancy: TextView
    private lateinit var medicineDayPregn: RecyclerView
    private lateinit var doctorsVisit: RecyclerView
    private lateinit var additionalInfoPregnancy: Button
    private lateinit var dayPregnancySettingButton: ImageButton
    private lateinit var dayPregnancyAcountButton: ImageButton
    private lateinit var homeButtonDayPregnancy: ImageButton

    private lateinit var medicineAdapter: MedicineAdapter
    private lateinit var doctorAdapter: DoctorVisitsAdapter

    private val medicines = mutableListOf<Medicine>()
    private val doctors = mutableListOf<DoctorVisit>()

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var selectedDate: LocalDate

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_pregnancy)

        // Initialize Views
        dateDayPregnancy = findViewById(R.id.dateDayPregnancy)
        cycleDayPregnancy = findViewById(R.id.cycleDayPregnancy)
        additionalInfoPregnancy = findViewById(R.id.additionalInfoPregnancy)
        dayPregnancySettingButton = findViewById(R.id.dayPregnancySettingButton)
        dayPregnancyAcountButton = findViewById(R.id.dayPregnancyAcountButton)
        homeButtonDayPregnancy = findViewById(R.id.homeButtonCalendar)
        medicineDayPregn = findViewById(R.id.medicineDayPregn)
        doctorsVisit = findViewById(R.id.doctorsVisit)

        // Set layout managers for RecyclerViews
        medicineDayPregn.layoutManager = LinearLayoutManager(this)
        doctorsVisit.layoutManager = LinearLayoutManager(this)

        // Initialize Adapters
        medicineAdapter = MedicineAdapter(medicines) { medicine ->
            saveMedicineCheckStatus(medicine)
        }
        doctorAdapter = DoctorVisitsAdapter(doctors) { doctor ->
            saveDoctorCheckStatus(doctor)
        }

        medicineDayPregn.adapter = medicineAdapter
        doctorsVisit.adapter = doctorAdapter

        userId = intent.getStringExtra("USER_ID") ?: ""
        selectedDate = LocalDate.parse(intent.getStringExtra("SELECTED_DATE"))
        db = FirebaseFirestore.getInstance()

        dateDayPregnancy.text = LocalDate.now().toString()

        additionalInfoPregnancy.setOnClickListener {
            openAdditionalInformationActivity(userId, selectedDate)
        }
        dayPregnancySettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
        dayPregnancyAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }
        homeButtonDayPregnancy.setOnClickListener {
            openHomeWindowActivity(userId)
        }

        fetchPregnancyData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAdditionalInformationActivity(userId: String, date: LocalDate) {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
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

    private fun openHomeWindowActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchPregnancyData() {
        db.collection("users").document(userId).collection("pregnancies")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val pregnancy = document.toObject(Pregnancy::class.java)
                    val startDate = pregnancy.startDatePregnancy

                    if (startDate != null) {
                        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")
                        val startDateLocalDate = LocalDate.parse(startDate.toString(), formatter)
                        val currentWeek = calculateCurrentWeek(startDateLocalDate, selectedDate)
                        cycleDayPregnancy.text = currentWeek.toString()
                        fetchMedicines()
                        return@addOnSuccessListener
                    }
                }
                cycleDayPregnancy.text = "N/A"
            }
            .addOnFailureListener { exception ->
                Log.e("DayPregnancyActivity", "Error getting pregnancy documents: ", exception)
                cycleDayPregnancy.text = "Error"
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateCurrentWeek(startDate: LocalDate, selectedDate: LocalDate): Long {
        return ChronoUnit.WEEKS.between(startDate, selectedDate) + 1 
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                // Fetch today's status
                fetchTodaysMedicineStatus()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

    private fun fetchDoctors() {
        db.collection("users").document(userId).collection("doctors")
            .get()
            .addOnSuccessListener { result ->
                doctors.clear()
                for (document in result) {
                    val doctor = DoctorVisit(
                        id = document.id,
                        doctorName = document.getString("Doctor_name") ?: "",
                        visitDate = document.getString("visitDate") ?: "",
                        isChecked = document.getBoolean("isChecked") ?: false
                    )
                    doctors.add(doctor)
                }
                doctorAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDoctorCheckStatus(doctor: DoctorVisit) {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(LocalDate.now().toString())
            .collection("doctors")
            .document(doctor.id)
            .set(mapOf("checked" to doctor.isChecked))
            .addOnSuccessListener {
                Toast.makeText(this, "Doctor status updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
