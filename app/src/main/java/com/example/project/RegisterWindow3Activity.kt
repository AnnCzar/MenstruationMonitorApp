package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterWindow3Activity : AppCompatActivity() {
    private lateinit var enterMedicineName: EditText
    private lateinit var enterDoseMedicineRegister: EditText
    private lateinit var enterTimeMedicineRegister: EditText
    private lateinit var buttonConfirmRegisterWindow3: Button
    private lateinit var buttonSaveMedicineRegisterWindow3: Button

    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window3)

        // znalezienie elementów
        enterMedicineName = findViewById(R.id.enterMedicineName)
        enterDoseMedicineRegister = findViewById(R.id.enterDoseMedicineRegister)
        enterTimeMedicineRegister = findViewById(R.id.enterTimeMedicineRegister)
        buttonConfirmRegisterWindow3 = findViewById(R.id.buttonConfirmRegisterWindow3)
        buttonSaveMedicineRegisterWindow3 = findViewById(R.id.buttonSaveMedicineRegisterWindow3)

        // Pobranie userId z intent
        val userId = intent.getStringExtra("USER_ID")

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięcia przycisku
        buttonSaveMedicineRegisterWindow3.setOnClickListener {
            val medicineName = enterMedicineName.text.toString()
            val doseMedicine = enterDoseMedicineRegister.text.toString()
            val timeMedicine = enterTimeMedicineRegister.text.toString()

            if (medicineName.isNotEmpty() && doseMedicine.isNotEmpty() && timeMedicine.isNotEmpty()) {
                // Utworzenie mapy z danymi dotyczącymi leku
                val medicineDetails = mapOf(
                    "medicineName" to medicineName,
                    "doseMedicine" to doseMedicine,
                    "timeMedicine" to timeMedicine
                )

                // Uruchomienie korutyny w wątku głównym
                GlobalScope.launch(Dispatchers.Main) {
                    // Dodanie danych dotyczących leku do bazy danych Firestore
                    db.collection("users").document(userId!!)
                        .collection("medicines").add(medicineDetails)
                        .addOnSuccessListener {
                            Toast.makeText(this@RegisterWindow3Activity, "Lek zapisany", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@RegisterWindow3Activity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                // Wyświetlenie komunikatu o błędzie
                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
            }
        }

        buttonConfirmRegisterWindow3.setOnClickListener {
            openMainWindowPeriodActivity()
        }
    }

    private fun openMainWindowPeriodActivity() {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        startActivity(intent)
    }
}
