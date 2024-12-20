package com.example.project

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
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
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

/**
 * Activity for adding a new doctor visit to the Firestore database.
 * Allows the user to input the doctor's name, visit date, time, address, and additional information.
 */
class AddVisitActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var doctorNameEditText: EditText
    private lateinit var visitDateEditText: TextInputEditText
    private lateinit var extrInformationEditText: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var addVisitConfirmButton: Button
    private lateinit var addresText: EditText

    /**
     * Called when the activity is first created.
     * Sets up UI components and event listeners for the activity.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
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
        addresText = findViewById(R.id.addressText)

        visitDateEditText.setOnClickListener{
            showDatePickerDialog()
        }

        addVisitConfirmButton.setOnClickListener {
            val doctorName = doctorNameEditText.text.toString()

            if (doctorName.isNotEmpty()) {
                addNewDoctorVisit()
            } else {
                Toast.makeText(this, "Błąd", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Displays a date picker dialog for the user to select a visit date.
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
                visitDateEditText.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    /**
     * Retrieves the selected time from the time picker as a formatted string.
     *
     * @param timePicker The TimePicker UI component.
     * @return A string representing the selected time in the format "HH:mm".
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimeFromTimePicker(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val time = LocalTime.of(hour, minute)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return time.format(formatter)
    }

    /**
     * Adds a new doctor visit to the Firestore database.
     * Collects data from UI components and creates a new document in the "doctorVisits" collection.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNewDoctorVisit() {
        val dateStr = visitDateEditText.text.toString()
        val doctorName = doctorNameEditText.text.toString()
        val extraInfo = extrInformationEditText.text.toString()
        val address = addresText.text.toString()
        val selectedTime = getTimeFromTimePicker(timePicker)

        if (dateStr == null) {
            Toast.makeText(this, "Niepoprawny format daty", Toast.LENGTH_SHORT).show()
            return
        }

        val newDocRef = db.collection("users").document(userId)
            .collection("doctorVisits").document()

        val newDoctorVisit = DoctorVisit(
            id = newDocRef.id,
            doctorName = doctorName,
            visitDate = dateStr,
            time = selectedTime,
            isChecked = false,
            extraInfo = extraInfo,
            address = address
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
