
package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PeriodBegginingActivity : AppCompatActivity() {

    private lateinit var period_beg_text: TextView
    private lateinit var insert_date_period_beg: TextInputEditText
    private lateinit var accept_period_beg_button: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.period_beggining)

        period_beg_text = findViewById(R.id.period_beg_text)
        insert_date_period_beg = findViewById(R.id.insert_date_period_start)
        accept_period_beg_button = findViewById(R.id.accept_period_beg_button)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        insert_date_period_beg.setOnClickListener {
            showDatePickerDialog()
        }

        accept_period_beg_button.setOnClickListener {
            addCycleDataToFirestore()
            val cyclePrediction = CyclePrediction(db)

            cyclePrediction.predictNextMenstruation(userId)  // nowa data na nastepną menstraucaje zapsiana do bazy

//            openMainWindowPeriod(userId)

        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                insert_date_period_beg.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addCycleDataToFirestore() {
        val dateStr = insert_date_period_beg.text.toString()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate: Date? = sdf.parse(dateStr)

        if (startDate == null) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            return
        }

        val startDateLocalDate = startDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val cycleData = HashMap<String, Any>()

        db.collection("users").document(userId).collection("cycles")
            .orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val latestCycle = documents.first()

                    // Zmieniamy na pobieranie jako string i parsowanie poprawnym formatem
                    val startDateOldString = latestCycle.getString("startDate")
                    val startDateOld: Date? = startDateOldString?.let { sdf.parse(it) }

                    if (startDateOld != null) {
                        val startDateOldLocalDate = startDateOld.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        // Obliczenie długości cyklu (liczba dni między poprzednią a nową datą)
                        val numberOfDays = ChronoUnit.DAYS.between(startDateOldLocalDate, startDateLocalDate)
                        cycleData["cycleLength"] = numberOfDays
                    }
                }

                // Obliczenie następnej daty menstruacji i owulacji
                val nextPeriodDate = calculateNextPeriodDate(startDateLocalDate)
                val ovulationDate = calculateOvulationDate(nextPeriodDate)

                // Zapis daty jako String w formacie "yyyy-MM-dd"
                cycleData["userId"] = userId
                cycleData["startDate"] = sdf.format(startDate) // Zapisanie daty jako string
                cycleData["nextPeriodDate"] = nextPeriodDate.toString()
                cycleData["nextOvulationDate"] = ovulationDate.toString()

                // Zapis danych cyklu do Firestore
                db.collection("users").document(userId)
                    .collection("cycles").document()
                    .set(cycleData)
                    .addOnSuccessListener {
                        Toast.makeText(this@PeriodBegginingActivity, "Dane cyklu dodane do Firestore", Toast.LENGTH_SHORT).show()

                        openMainWindowPeriod(userId)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@PeriodBegginingActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@PeriodBegginingActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun openMainWindowPeriod(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

}
