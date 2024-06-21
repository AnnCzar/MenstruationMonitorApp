package com.example.project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.project.R.id.medicinePregnancy
import com.google.firebase.firestore.FirebaseFirestore
import database.collections.Pregnancy
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class MainWindowPregnancyActivity : AppCompatActivity() {
    private lateinit var daysLeftPregnancy: TextView
    private lateinit var medicinePregnancy: RecyclerView
    private lateinit var toCalendarButtonPregn: Button
    private lateinit var mainWindowPregnancySettingButton: ImageButton
    private lateinit var mainWindowPregnancyAccountButton: ImageButton
    private lateinit var logoutButtonPregnancy: Button
    private lateinit var dateTextPregnancy: TextView
    private lateinit var endingPregnancyButton: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

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

        toCalendarButtonPregn = findViewById(R.id.toCalendarButtonPregn)
        mainWindowPregnancySettingButton = findViewById(R.id.mainWindowPregnancySettingButton)
        mainWindowPregnancyAccountButton = findViewById(R.id.mainWindowPregnancyAcountButton)
        logoutButtonPregnancy = findViewById(R.id.logoutButtonPregnancy)
        dateTextPregnancy = findViewById(R.id.dateTextPregnancy)
        daysLeftPregnancy = findViewById(R.id.daysLeftPregnancy)
        medicinePregnancy = findViewById(R.id.medicinePregnancy)
        endingPregnancyButton = findViewById(R.id.endingPregnancyButton)
        medicinePregnancy.layoutManager = LinearLayoutManager(this)

        medicineAdapter = MedicineAdapter(medicines) { medicine ->
            saveMedicineCheckStatus(medicine)
        }
        medicinePregnancy.adapter = medicineAdapter

        dateTextPregnancy.text = LocalDate.now().toString()

        fetchPregnancyData()

        logoutButtonPregnancy.setOnClickListener {
            logout()
        }

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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchPregnancyData() {
        db.collection("pregnancies")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val pregnancy = document.toObject(Pregnancy::class.java)
                    val startDate = pregnancy.startDatePregnancy

                    if (startDate != null) {
                        val endDate = calculateEndDate(startDate)
                        val daysLeft = calculateDaysLeft(endDate)
                        daysLeftPregnancy.text = daysLeft.toString()
                        fetchMedicines()
                        return@addOnSuccessListener
                    }
                }
                daysLeftPregnancy.text = "N/A"
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting pregnancy documents: ", exception)
                // Handle error case
                daysLeftPregnancy.text = "Error"
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
                Log.e(TAG, "Error fetching medicines: ", e)
                // Handle error case
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

    private fun openAccountWindowActivity() {
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
