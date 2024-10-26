package com.example.project

import android.content.Intent
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DayPeriodActivity : AppCompatActivity() {

    private lateinit var cycleDayPeriod: TextView
    private lateinit var additionalInfoPeriod: Button

    private lateinit var dayPeriodSettingsButton: ImageButton
    private lateinit var dayPeriodAcountButton: ImageButton
    private lateinit var dateDayPeriod: TextView
    private lateinit var dayPeriodHomeButton: ImageButton

    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var doctorRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private lateinit var doctorAdapter: DoctorVisitAdapter// Adapter for RecyclerView
    class DoctorVisitAdapter(
        private val visits: List<DoctorVisit>,
        private val onVisitClick: (DoctorVisit) -> Unit
    ) : RecyclerView.Adapter<DoctorVisitAdapter.DoctorVisitViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return DoctorVisitViewHolder(view)
        }

        override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
            val visit = visits[position]
            holder.bind(visit)
            holder.itemView.setOnClickListener { onVisitClick(visit) }
        }

        override fun getItemCount(): Int = visits.size

        class DoctorVisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(android.R.id.text1)

            fun bind(visit: DoctorVisit) {
                textView.text = "${visit.doctorName} - ${visit.visitDate}"
            }
        }
    }

    private lateinit var drinksCountText: TextView
    private lateinit var increaseDrinkButton: Button
    private lateinit var decreaseDrinkButton: Button

    private var drinksCount: Int = 0

    private val medicines = mutableListOf<Medicine>()
    private val doctors = mutableListOf<DoctorVisit>()

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var selectedDate: LocalDate

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Get userId from Intent
        userId = intent.getStringExtra("USER_ID") ?: ""
        selectedDate = LocalDate.parse(intent.getStringExtra("SELECTED_DATE"))

        // Initialize views
        cycleDayPeriod = findViewById(R.id.cycleDayPeriod)
        additionalInfoPeriod = findViewById(R.id.additionalInfoPeriod)
        drinksCountText = findViewById(R.id.drinksCountText)
        increaseDrinkButton = findViewById(R.id.increaseDrinkButton)
        decreaseDrinkButton = findViewById(R.id.decreaseDrinkButton)

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
        doctorAdapter = DoctorVisitAdapter(doctors) { doctor ->
            saveDoctorCheckStatus(doctor)
        }

        // Set Adapters
        medicineRecyclerView.adapter = medicineAdapter
        doctorRecyclerView.adapter = doctorAdapter

        // Fetch data
        fetchMedicines()
        fetchDoctorVisits()
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


        fetchTodaysCycleDay()
    }

    private fun updateDrinkCount() {
        drinksCountText.text = drinksCount.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTodaysCycleDay() {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    if (userDoc.contains("lastPeriodDate")) {
                        val lastPeriodDateValue = userDoc.get("lastPeriodDate")

                        if (lastPeriodDateValue is String) {
                            val lastPeriodDate = LocalDate.parse(lastPeriodDateValue, DateTimeFormatter.ISO_LOCAL_DATE)
                            val cycleLength = userDoc.getLong("cycleLength")?.toInt()

                            if (cycleLength != null) {
                                val currentDate = LocalDate.now()
                                val cycleDay = calculateCycleDay(lastPeriodDate, currentDate, cycleLength)
                                Log.d("CycleDay", "cycleDay calculated: $cycleDay")
                                displayCycleDay(cycleDay)
                            } else {
                                Toast.makeText(this, "No valid cycleLength found", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val lastPeriodDate = parseCustomDate(lastPeriodDateValue)

                            if (lastPeriodDate != null) {
                                val cycleLength = userDoc.getLong("cycleLength")?.toInt()

                                if (cycleLength != null) {
                                    val currentDate = LocalDate.now()
                                    val cycleDay = calculateCycleDay(lastPeriodDate, currentDate, cycleLength)
                                    Log.d("CycleDay", "cycleDay calculated: $cycleDay")
                                    displayCycleDay(cycleDay)
                                } else {
                                    Toast.makeText(this, "No valid cycleLength found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "'lastPeriodDate' is not a valid date format in Firestore", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "'lastPeriodDate' field does not exist in Firestore", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseCustomDate(dateValue: Any?): LocalDate? {
        return try {
            when (dateValue) {
                is String -> LocalDate.parse(dateValue, DateTimeFormatter.ISO_LOCAL_DATE)
                is Map<*, *> -> {
                    val year = (dateValue["year"] as? Long)?.toInt() ?: return null
                    val month = (dateValue["monthValue"] as? Long)?.toInt() ?: return null
                    val day = (dateValue["dayOfMonth"] as? Long)?.toInt() ?: return null
                    LocalDate.of(year, month, day)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateCycleDay(lastPeriodDate: LocalDate, currentDate: LocalDate, cycleLength: Int): Int {
        val daysPassed = ChronoUnit.DAYS.between(lastPeriodDate, currentDate)
        var cycleDay = ((daysPassed % cycleLength) + cycleLength) % cycleLength + 1

        if (cycleDay <= 0) {
            cycleDay += cycleLength
        }

        return cycleDay.toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayCycleDay(cycleDay: Int) {
        Log.d("UI", "Updating UI with cycleDay: $cycleDay")
        runOnUiThread {
            cycleDayPeriod.text = "$cycleDay"
        }
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
            .document(selectedDate.toString()).collection("medicines")
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
                    )
                    doctors.add(doctor)
                }
                doctorAdapter.notifyDataSetChanged() // Aktualizacja adaptera po zmianie danych
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTodaysDoctorVisitsStatus() {
        val today = LocalDate.now().toString()
        db.collection("users").document(userId).collection("dailyInfo")
            .document(today).collection("doctorVisits")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val visitId = document.id
                    val isChecked = document.getBoolean("checked") ?: false
                    medicines.find { it.id == visitId }?.isChecked = isChecked
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
            .document(selectedDate.toString())
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
            .document(selectedDate.toString())
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
}
