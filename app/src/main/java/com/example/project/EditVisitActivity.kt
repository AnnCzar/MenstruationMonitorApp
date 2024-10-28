//package com.example.project
//
//import android.os.Build
//import android.os.Bundle
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.textfield.TextInputEditText
//import com.google.firebase.firestore.FirebaseFirestore
//import android.widget.EditText
//import android.widget.TimePicker
//import android.util.Log
//import java.text.SimpleDateFormat
//import java.time.LocalTime
//import java.time.format.DateTimeFormatter
//import java.util.*
//
//class EditVisitActivity : AppCompatActivity() {
//
//    private lateinit var db: FirebaseFirestore
//    private lateinit var userId: String
//    private lateinit var visitId: String
//
//    private lateinit var doctorNameEditText: EditText
//    private lateinit var visitDateEditText: TextInputEditText
//    private lateinit var extrInformationEditText: EditText
//    private lateinit var timePicker: TimePicker
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.modify_visit)
//
//        // Initialize Firestore and get the user and visit IDs from the Intent
//        db = FirebaseFirestore.getInstance()
//        userId = intent.getStringExtra("USER_ID") ?: ""
//        visitId = intent.getStringExtra("VISIT_ID") ?: ""
//
//        // Initialize UI components
//        doctorNameEditText = findViewById(R.id.doctorNameEditText)
//        visitDateEditText = findViewById(R.id.insert_date_period_start)
//        extrInformationEditText = findViewById(R.id.extrInformationEditText)
//        timePicker = findViewById(R.id.timePicker1)
//
//        // Retrieve the visit data and populate the form
//        getVisitData()
//    }
//
//    // Function to retrieve visit data from Firestore and populate the form
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun getVisitData() {
//        db.collection("users").document(userId)
//            .collection("doctorVisits").document(visitId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    // Extract data from the document
//                    val doctorName = document.getString("doctorName") ?: ""
//                    val visitDate = document.getString("visitDate") ?: ""
//                    val extraInfo = document.getString("extraInfo") ?: ""
//                    val visitTime = document.getString("time") ?: ""
//
//                    // Populate the fields with the retrieved data
//                    doctorNameEditText.setText(doctorName)
//                    visitDateEditText.setText(visitDate)
//                    extrInformationEditText.setText(extraInfo)
//
//                    // Parse the time and set it on the TimePicker
//                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//                    val localTime = LocalTime.parse(visitTime, timeFormatter)
//                    timePicker.hour = localTime.hour
//                    timePicker.minute = localTime.minute
//
//                } else {
//                    Toast.makeText(this, "Wizyta nie znaleziona", Toast.LENGTH_SHORT).show()
//                    finish() // Close the activity if the visit doesn't exist
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("EditVisitActivity", "Błąd podczas pobierania wizyty", e)
//                Toast.makeText(this, "Błąd podczas pobierania danych wizyty", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
