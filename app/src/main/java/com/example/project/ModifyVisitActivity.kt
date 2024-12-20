package com.example.project

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Activity for modifying doctor visit details.
 *
 * This activity allows users to view and edit doctor visit details such as doctor's name, visit date, time, address, and additional information.
 * It retrieves visit information from Firebase Firestore, displays it for editing, and saves any modifications back to the database.
 */
class ModifyVisitActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var visitId: String

    private lateinit var doctorNameEditText: EditText
    private lateinit var visitDateEditText: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var addresText: EditText
    private lateinit var saveButton: Button
    private lateinit var extrInformationEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modify_visit)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("USER_ID") ?: ""
        visitId = intent.getStringExtra("VISIT_ID") ?: ""

        doctorNameEditText = findViewById(R.id.doctorNameEditText)
        visitDateEditText = findViewById(R.id.visitDateEditText)
        saveButton = findViewById(R.id.saveButton)
        timePicker = findViewById(R.id.timePicker1)
        addresText = findViewById(R.id.eddresModify)
        extrInformationEditText = findViewById(R.id.extrInformationEditText)


        loadVisitDetails()

        saveButton.setOnClickListener {
            saveVisitDetails()
        }
    }

    /**
     * Loads the details of the selected doctor visit from Firestore and displays them in the respective EditTexts.
     * If the visit details are not found, shows a message and finishes the activity.
     */
    private fun loadVisitDetails() {
        db.collection("users").document(userId).collection("doctorVisits").document(visitId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    doctorNameEditText.setText(document.getString("doctorName"))
                    visitDateEditText.setText(document.getString("visitDate"))
                  val timeString = document.getString("time")
                    if (!timeString.isNullOrEmpty()) {
                        val timeParts = timeString.split(":")
                        if (timeParts.size == 2) {
                            val hour = timeParts[0].toInt()
                            val minute = timeParts[1].toInt()
                            timePicker.setHour(hour)  // Ustawiamy godzinę
                            timePicker.setMinute(minute)  // Ustawiamy minutę
                        }
                    }



                    addresText.setText(document.getString("address"))
                    extrInformationEditText.setText(document.getString("extraInfo"))

                } else {
                    Toast.makeText(this, "Brak danych dla wizyty", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    /**
     * Saves the modified doctor visit details to Firestore.
     * Checks if any required field is empty and shows a message if necessary.
     * On successful save, shows a success message and finishes the activity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveVisitDetails() {
        val doctorName = doctorNameEditText.text.toString()
        val visitDate = visitDateEditText.text.toString()
        val selectedTime = getTimeFromTimePicker(timePicker)

        val address = addresText.text.toString()
        val extraInfo = extrInformationEditText.text.toString()



        if (doctorName.isEmpty() || visitDate.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie wymagane pola", Toast.LENGTH_SHORT).show()
            return
        }

        val visit = mapOf(
            "doctorName" to doctorName,
            "visitDate" to visitDate,
            "address" to address,
            "time" to selectedTime,
            "extraInfo" to extraInfo
        )

        db.collection("users").document(userId).collection("doctorVisits").document(visitId)
            .update(visit)
            .addOnSuccessListener {
                Toast.makeText(this, "Dane wizyty zostały zaktualizowane", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Converts the selected time from the TimePicker into a string formatted as "HH:mm".
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimeFromTimePicker(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val time = LocalTime.of(hour, minute)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return time.format(formatter)
    }


}
