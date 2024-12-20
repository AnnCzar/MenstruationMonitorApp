package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * PregnancyBegginingActivity handles the UI and logic for recording the start date of a pregnancy
 * and updating the user's pregnancy status in the Firestore database.
 */
class PregnancyBegginingActivity : AppCompatActivity() {
    private lateinit var pregnancy_beg_text: TextView
    private lateinit var insert_date_pregnancy_start: TextInputEditText
    private lateinit var accept_pregnancy_beg_button: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    @RequiresApi(Build.VERSION_CODES.O)
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

    /**
     * Displays a DatePickerDialog allowing the user to select the pregnancy start date.
     */
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
                // Formatowanie wybranej daty do pola tekstowego
                insert_date_pregnancy_start.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    /**
     * Saves the pregnancy start date to Firestore, updates the user's pregnancy status,
     * and calculates the estimated end date for the pregnancy (266 days later).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePregnancyStartDate() {
        val userRef = db.collection("users").document(userId)
        userRef
            .update("statusPregnancy", true)
            .addOnSuccessListener {
                Toast.makeText(this@PregnancyBegginingActivity, "Status ciąży zaktualizowany na true", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@PregnancyBegginingActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        val dateStr = insert_date_pregnancy_start.text.toString().trim()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.isLenient = false
        val startDate: Date? = try {
            sdf.parse(dateStr)
        } catch (e: ParseException) {
            null
        }

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
                    Toast.makeText(this, "Zapisano datę początku ciąży", Toast.LENGTH_SHORT).show()
                    openMainWindowPregnancyActivity(userId)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Nieprawidłowy format daty. Użyj RRRR-MM-dd", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the main pregnancy window activity after successfully saving the pregnancy data.
     *
     * @param userId The ID of the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }
}
