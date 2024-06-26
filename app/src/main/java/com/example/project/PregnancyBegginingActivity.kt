package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PregnancyBegginingActivity : AppCompatActivity() {
    private lateinit var pregnancy_beg_text: TextView
    private lateinit var insert_date_pregnancy_start: TextInputEditText
    private lateinit var accept_pregnancy_beg_button: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pregnancy_beggining)

        pregnancy_beg_text = findViewById(R.id.pregnancy_beg_text)
        insert_date_pregnancy_start = findViewById(R.id.insert_date_pregnancy_start)
        accept_pregnancy_beg_button = findViewById(R.id.accept_pregnancy_beg_button)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        insert_date_pregnancy_start.setOnClickListener {
            showDatePickerDialog()
        }

        accept_pregnancy_beg_button.setOnClickListener {
            savePregnancyStartDate()
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
                insert_date_pregnancy_start.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun savePregnancyStartDate() {
        val dateStr = insert_date_pregnancy_start.text.toString()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate: Date? = sdf.parse(dateStr)

        if (startDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = startDate
            calendar.add(Calendar.DAY_OF_YEAR, 266)
            val endDate = calendar.time

            val pregnancyData = hashMapOf(
                "startDatePregnancy" to startDate,
                "endDatePregnancy" to endDate,
                "userId" to userId
            )

            db.collection("users").document(userId).collection("pregnancies")
                .add(pregnancyData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pregnancy start date saved", Toast.LENGTH_SHORT).show()
                    openMainWindowPregnancyActivity(userId)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
