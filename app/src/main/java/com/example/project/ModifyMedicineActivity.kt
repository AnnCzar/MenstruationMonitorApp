package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ModifyMedicineActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var medicineId: String
    private lateinit var medicineName: EditText
    private lateinit var medicineDose: EditText
    private lateinit var medicineTime: EditText

    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modify_medicine)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("USER_ID") ?: ""
        medicineId = intent.getStringExtra("MEDICINE_ID") ?: ""

        medicineName = findViewById(R.id.enterMedicineName)
        medicineDose = findViewById(R.id.enterDoseMedicineRegister)
        medicineTime = findViewById(R.id.enterTimeMedicineRegister)
        saveButton = findViewById(R.id.buttonSaveMedicineRegisterWindow3)

        loadMedicineDetails()

        saveButton.setOnClickListener {
            saveMedicineDetails()
        }
    }


    private fun loadMedicineDetails() {
        db.collection("users").document(userId).collection("medicines").document(medicineId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    medicineName.setText(document.getString("medicineName"))
                    medicineDose.setText(document.getString("doseMedicine"))
                    medicineTime.setText(document.getString("timeMedicine"))
                } else {
                    Toast.makeText(this, "Nie znaleziono leków", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }


    private fun saveMedicineDetails() {
        val medicineName = medicineName.text.toString()
        val doseMedicine = medicineDose.text.toString()
        val timeMedicine = medicineTime.text.toString()

        if (medicineName.isEmpty() || doseMedicine.isEmpty() || timeMedicine.isEmpty()) {
            Toast.makeText(this, "Proszę uzupełnić wszystkie pola", Toast.LENGTH_SHORT).show()
            return
        }

        val medicine = hashMapOf(
            "medicineName" to medicineName,
            "doseMedicine" to doseMedicine,
            "timeMedicine" to timeMedicine
        )

        db.collection("users").document(userId).collection("medicines").document(medicineId)
            .set(medicine)
            .addOnSuccessListener {
                Toast.makeText(this, "Pomyślnie zmodyfikowano leki", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
