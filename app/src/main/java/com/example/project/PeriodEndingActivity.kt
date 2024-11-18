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
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class PeriodEndingActivity : AppCompatActivity(){
    private lateinit var period_end_text: TextView
    private lateinit var insert_date_period_end: TextInputEditText
    private lateinit var accept_period_end_button: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.period_ending)

        period_end_text = findViewById(R.id.period_end_text)
        insert_date_period_end = findViewById(R.id.insert_date_period_end)
        accept_period_end_button = findViewById(R.id.accept_period_end_button)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()


        insert_date_period_end.setOnClickListener {
            showDatePickerDialog()
        }

        accept_period_end_button.setOnClickListener {
            savePeriodEndDate()
            val cyclePrediction = CyclePrediction(db)
            cyclePrediction.calculateMedianCycleLength(userId){
                    medianCycles -> Log.d("aaa", medianCycles.toString())

            }
            cyclePrediction.predictNextMenstruation(userId)  // nowa data na nastepną menstraucaje zapsiana do bazy

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
                insert_date_period_end.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePeriodEndDate() {   // tutaj dac nowy algorytm

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
                                Toast.makeText(this@PeriodEndingActivity, "Data zakończenia okresu zaktualizowana", Toast.LENGTH_SHORT).show()
                                openMainWindowPregnancyActivity(userId)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@PeriodEndingActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@PeriodEndingActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }

}
