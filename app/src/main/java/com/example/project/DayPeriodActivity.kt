//package com.example.project
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.FirebaseFirestore
//import java.time.LocalDate
//
//class DayPeriodActivity : AppCompatActivity() {
//
//    private lateinit var cycleDayPeriod: TextView
//    private lateinit var additionalInfoPeriod: Button
//    private lateinit var buttonMinus: Button
//    private lateinit var buttonPlus: Button
//    private lateinit var dayPeriodSettingsButton: ImageButton
//    private lateinit var dayPeriodAcountButton: ImageButton
//    private lateinit var dateDayPeriod: TextView
//    private lateinit var dayPeriodHomeButton: ImageButton
//
//    private lateinit var medicineRecyclerView: RecyclerView
//    private lateinit var doctorRecyclerView: RecyclerView
//    private lateinit var medicineAdapter: MedicineAdapter
//    private lateinit var doctorAdapter: DoctorVisitsAdapter
//
//    private val medicines = mutableListOf<Medicine>()
//    private val doctors = mutableListOf<DoctorVisit>()
//
//    private lateinit var db: FirebaseFirestore
//    private lateinit var userId: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.day_period)
//
//        dayPeriodHomeButton = findViewById(R.id.dayPeriodHomeButton)
//        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
//        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
//        buttonMinus = findViewById(R.id.buttonMinus)
//        buttonPlus = findViewById(R.id.buttonPlus)
//        dayPeriodSettingsButton = findViewById(R.id.dayPeriodSettingsButton)
//        dayPeriodAcountButton = findViewById(R.id.dayPeriodAcountButton)
//        dateDayPeriod = findViewById(R.id.dateDayPeriod)
//
//        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
//        doctorRecyclerView = findViewById(R.id.doctorsRecyclerView)
//
//        userId = intent.getStringExtra("USER_ID") ?: ""
//        db = FirebaseFirestore.getInstance()
//
//        currentDate()
//
//        // Initialize RecyclerViews
//        medicineRecyclerView.layoutManager = LinearLayoutManager(this)
//        doctorRecyclerView.layoutManager = LinearLayoutManager(this)
//
//        medicineAdapter = MedicineAdapter(medicines) { medicine ->
//            saveMedicineCheckStatus(medicine)
//        }
//        doctorAdapter = DoctorVisitsAdapter(doctors) { doctor ->
//            saveDoctorCheckStatus(doctor)
//        }
//
//        medicineRecyclerView.adapter = medicineAdapter
//        doctorRecyclerView.adapter = doctorAdapter
//
//        fetchMedicines()
//        fetchDoctors()
//
//        // Button click listeners
//        additionalInfoPeriod.setOnClickListener {
//            openAdditionalInformationActivity(userId)
//        }
//
//        dayPeriodAcountButton.setOnClickListener {
//            openAccountWindowActivity(userId)
//        }
//
//        dayPeriodSettingsButton.setOnClickListener {
//            openSettingsWindowActivity(userId)
//        }
//
//        dayPeriodHomeButton.setOnClickListener {
//            openHomeWindowActivity(userId)
//        }
//
//        buttonMinus.setOnClickListener {
//            // Handle minus button click
//        }
//
//        buttonPlus.setOnClickListener {
//            // Handle plus button click
//        }
//        fetchTodaysMedicineStatus()
//    }
//
//    private fun currentDate() {
//        val currentDate = LocalDate.now()
//        cycleDayPeriod.text = currentDate.toString()
//    }
//    private fun fetchMedicines() {
//        db.collection("users").document(userId).collection("medicines")
//            .get()
//            .addOnSuccessListener { result ->
//                medicines.clear()
//                for (document in result) {
//                    val medicine = Medicine(
//                        id = document.id,
//                        name = document.getString("medicineName") ?: "",
//                        isChecked = false // Initialize isChecked to false
//                    )
//                    medicines.add(medicine)
//                }
//                // Fetch today's status
//                fetchTodaysMedicineStatus()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun fetchTodaysMedicineStatus() {
//        val today = LocalDate.now().toString()
//        db.collection("users").document(userId).collection("dailyInfo")
//            .document(today).collection("medicines")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val medicineId = document.id
//                    val isChecked = document.getBoolean("checked") ?: false
//                    medicines.find { it.id == medicineId }?.isChecked = isChecked
//                }
//                medicineAdapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun fetchDoctors() {
//        db.collection("users").document(userId).collection("doctors")
//            .get()
//            .addOnSuccessListener { result ->
//                doctors.clear()
//                for (document in result) {
//                    val doctor = DoctorVisit(
//                        id = document.id,
//                        doctorName = document.getString("Doctor_name") ?: "",
//
//                        isChecked = document.getBoolean("isChecked") ?: false
//                    )
//                    doctors.add(doctor)
//                }
//                doctorAdapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun saveMedicineCheckStatus(medicine: Medicine) {
//        db.collection("users").document(userId)
//            .collection("dailyInfo")
//            .document(LocalDate.now().toString())
//            .collection("medicines")
//            .document(medicine.id)
//            .set(mapOf("checked" to medicine.isChecked))
//            .addOnSuccessListener {
//                Toast.makeText(this, "Medicine status updated", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun saveDoctorCheckStatus(doctor: DoctorVisit) {
//        db.collection("users").document(userId)
//            .collection("dailyInfo")
//            .document(LocalDate.now().toString())
//            .collection("doctors")
//            .document(doctor.id)
//            .set(mapOf("checked" to doctor.isChecked))
//            .addOnSuccessListener {
//                Toast.makeText(this, "Doctor status updated", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun openAdditionalInformationActivity(userId: String) {
//        val intent = Intent(this, AdditionalInformationActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//
//    private fun openSettingsWindowActivity(userId: String) {
//        val intent = Intent(this, SettingsWindowActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//
//    private fun openAccountWindowActivity(userId: String) {
//        val intent = Intent(this, AccountWindowActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//
//    private fun openHomeWindowActivity(userId: String) {
//        val intent = Intent(this, MainWindowPeriodActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//}
//
//
//
package com.example.project

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
import java.time.format.DateTimeFormatter

class DayPeriodActivity : AppCompatActivity() {

    private lateinit var cycleDayPeriod: TextView
    private lateinit var additionalInfoPeriod: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonPlus: Button
    private lateinit var dayPeriodSettingsButton: ImageButton
    private lateinit var dayPeriodAcountButton: ImageButton
    private lateinit var dateDayPeriod: TextView
    private lateinit var dayPeriodHomeButton: ImageButton

    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var doctorRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private lateinit var doctorAdapter: DoctorVisitsAdapter

    private val medicines = mutableListOf<Medicine>()
    private val doctors = mutableListOf<DoctorVisit>()

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Get userId from Intent
        userId = intent.getStringExtra("USER_ID") ?: ""
        var selectedDate = LocalDate.parse(intent.getStringExtra("SELECTED_DATE"))

        // Initialize views
        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
        buttonMinus = findViewById(R.id.buttonMinus)
        buttonPlus = findViewById(R.id.buttonPlus)
        dayPeriodSettingsButton = findViewById(R.id.dayPeriodSettingsButton)
        dayPeriodAcountButton = findViewById(R.id.dayPeriodAcountButton)
        dateDayPeriod = findViewById(R.id.dateDayPeriod)
        dayPeriodHomeButton = findViewById(R.id.dayPeriodHomeButton)

        // Initialize RecyclerViews
        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
        doctorRecyclerView = findViewById(R.id.doctorsRecyclerView)
        medicineRecyclerView.layoutManager = LinearLayoutManager(this)
        doctorRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapters
        medicineAdapter = MedicineAdapter(medicines) { medicine ->
            saveMedicineCheckStatus(medicine)
        }
        doctorAdapter = DoctorVisitsAdapter(doctors) { doctor ->
            saveDoctorCheckStatus(doctor)
        }

        // Set Adapters
        medicineRecyclerView.adapter = medicineAdapter
        doctorRecyclerView.adapter = doctorAdapter

        // Fetch data
        fetchMedicines()
        fetchDoctors()
        dateDayPeriod.text = selectedDate.toString()

        // Button click listeners
        additionalInfoPeriod.setOnClickListener {
            openAdditionalInformationActivity(userId, selectedDate)
        }

        dayPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        dayPeriodSettingsButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        dayPeriodHomeButton.setOnClickListener {
            openHomeWindowActivity(userId)
        }

        buttonMinus.setOnClickListener {
            // Handle minus button click
        }

        buttonPlus.setOnClickListener {
            // Handle plus button click
        }
    }

    private fun currentDate() {
        val currentDate = LocalDate.now()
        cycleDayPeriod.text = currentDate.toString()
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
                // Fetch today's status
                fetchTodaysMedicineStatus()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

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
}

