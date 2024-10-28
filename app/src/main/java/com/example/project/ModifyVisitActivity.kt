package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ModifyVisitActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var visitId: String

    private lateinit var doctorNameEditText: EditText
    private lateinit var visitDateEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modify_visit)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("USER_ID") ?: ""
        visitId = intent.getStringExtra("VISIT_ID") ?: ""

        doctorNameEditText = findViewById(R.id.doctorNameEditText)
        visitDateEditText = findViewById(R.id.visitDateEditText)
        saveButton = findViewById(R.id.saveButton)

        loadVisitDetails()

        saveButton.setOnClickListener {
            saveVisitDetails()
        }
    }

    private fun loadVisitDetails() {
        db.collection("users").document(userId).collection("doctorVisits").document(visitId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    doctorNameEditText.setText(document.getString("doctorName"))
                    visitDateEditText.setText(document.getString("visitDate"))
                } else {
                    Toast.makeText(this, "No visit found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun saveVisitDetails() {
        val doctorName = doctorNameEditText.text.toString()
        val visitDate = visitDateEditText.text.toString()

        if (doctorName.isEmpty() || visitDate.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val visit = hashMapOf(
            "doctorName" to doctorName,
            "visitDate" to visitDate
        )

        db.collection("users").document(userId).collection("doctorVisits").document(visitId)
            .set(visit)
            .addOnSuccessListener {
                Toast.makeText(this, "Visit modified successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
