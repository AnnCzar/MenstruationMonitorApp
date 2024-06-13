package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddVisitActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visit)

        // Inicjalizacja Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Pobranie userId z Intentu
        userId = intent.getStringExtra("USER_ID") ?: ""

        // Inicjalizacja widoków
        val doctorNameEditText: EditText = findViewById(R.id.doctorNameEditText)
        val visitDateEditText: EditText = findViewById(R.id.visitDateEditText)
        val addVisitConfirmButton: Button = findViewById(R.id.addVisitConfirmButton)

        // Obsługa kliknięcia przycisku "Dodaj wizytę"
        addVisitConfirmButton.setOnClickListener {
            val doctorName = doctorNameEditText.text.toString()
            val visitDate = visitDateEditText.text.toString()

            if (doctorName.isNotEmpty() && visitDate.isNotEmpty()) {
                addNewDoctorVisit(doctorName, visitDate)
            } else {
                // Obsługa walidacji pól, np. wyświetlenie komunikatu o błędzie
            }
        }
    }

    private fun addNewDoctorVisit(doctorName: String, visitDate: String) {
        val newDoctorVisit = DoctorVisit("", doctorName, visitDate, false)

        // Dodanie nowej wizyty do bazy danych
        db.collection("users").document(userId)
            .collection("doctorVisits")
            .add(newDoctorVisit)
            .addOnSuccessListener { documentReference ->
                newDoctorVisit.id = documentReference.id
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { exception ->
                // Obsługa błędów podczas dodawania wizyty do bazy danych
            }
    }
}
