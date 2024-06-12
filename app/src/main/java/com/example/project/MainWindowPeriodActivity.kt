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
import java.time.temporal.ChronoUnit

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

    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()

    private var isPeriodStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_period)

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

        begginingPregnancyButton = findViewById(R.id.begginingPregnancyButton)

        mainWindowPeriodSettingButton = findViewById(R.id.mainWindowPeriodSettingButton)
        mainWindowPeriodAcountButton = findViewById(R.id.mainWindowPeriodAcountButton)

        toCalendarButtonPeriod.setOnClickListener {
            openCalendarActivity(userId)
        }

        begginingPeriodButton.setOnClickListener {
            addCycleDataToFirestore()
            isPeriodStarted = true
            endPeriodButton.visibility = Button.VISIBLE
        }

        endPeriodButton.setOnClickListener {
            updateEndDateInFirestore()
        }

        begginingPregnancyButton.setOnClickListener {
            openPregnancyBegginingActivity(userId)
        }

        mainWindowPeriodAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        mainWindowPeriodSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
    }

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

    private fun calculateNextPeriodDate(currentDate: LocalDate): LocalDate {
        return currentDate.plusDays(28)
    }

    private fun calculateOvulationDate(nextPeriodDate: LocalDate): LocalDate {
        return nextPeriodDate.minusDays(14)
    }

    private fun updateEndDateInFirestore() {

        val cycleRef = db.collection("users").document(userId).collection("cycles").document(userId)

        if (isPeriodStarted) {
            cycleRef
                .update("endDate", LocalDate.now().toString())
                .addOnSuccessListener {
                    Toast.makeText(this@MainWindowPeriodActivity, "Data zakończenia okresu zaktualizowana", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@MainWindowPeriodActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this@MainWindowPeriodActivity, "Najpierw rozpocznij okres", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchMedicines() {
        db.collection("users").document(userId).collection("medicines")
            .get()
            .addOnSuccessListener { result ->
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
