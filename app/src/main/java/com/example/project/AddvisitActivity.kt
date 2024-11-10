package com.example.project

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddVisitActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var doctorNameEditText: EditText
    private lateinit var visitDateEditText: TextInputEditText
    private lateinit var extrInformationEditText: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var addVisitConfirmButton: Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visit)


        db = FirebaseFirestore.getInstance()

        userId = intent.getStringExtra("USER_ID") ?: ""


        doctorNameEditText = findViewById(R.id.doctorNameEditText)
        visitDateEditText = findViewById(R.id.insert_date_period_start)
        extrInformationEditText = findViewById(R.id.extrInformationEditText)
        timePicker = findViewById(R.id.timePicker1)
        addVisitConfirmButton = findViewById(R.id.addVisitConfirmButton)

        visitDateEditText.setOnClickListener{
            showDatePickerDialog()
        }

        addVisitConfirmButton.setOnClickListener {
            val doctorName = doctorNameEditText.text.toString()
            Log.d("dziala", doctorName)

            if (doctorName.isNotEmpty()) {
                // Dodanie wizyty z pełnymi danymi
                addNewDoctorVisit()
            } else {
                Toast.makeText(this, "cos nie kliklo", Toast.LENGTH_SHORT).show()
            }
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
                visitDateEditText.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimeFromTimePicker(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val time = LocalTime.of(hour, minute)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return time.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNewDoctorVisit() {
        val dateStr = visitDateEditText.text.toString()

        val doctorName = doctorNameEditText.text.toString()

        val extraInfo = extrInformationEditText.text.toString()

        val selectedTime = getTimeFromTimePicker(timePicker)


        if (dateStr == null) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            return
        }

        // tworzenie nowego dokuemntu aby miec id do doktork
        val newDocRef = db.collection("users").document(userId)
            .collection("doctorVisits").document()


        val newDoctorVisit = DoctorVisit(
            id = newDocRef.id,
            doctorName = doctorName,
            visitDate = dateStr,
            time = selectedTime,
            isChecked = false,
            extraInfo = extraInfo
        )
        newDocRef.set(newDoctorVisit)
            .addOnSuccessListener {
                setResult(RESULT_OK)
                Toast.makeText(this, "Zapisano wizytę", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Nie zapisano wizyty", Toast.LENGTH_SHORT).show()
            }

    }
}
