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

class RegisterWindow2Activity : AppCompatActivity() {
    private lateinit var enterLastPeriod: EditText
    private lateinit var cycleLen: EditText
    private lateinit var periodLen: EditText
    private lateinit var weightRegister: EditText
    private lateinit var buttonConfirmRegisterWindow2: Button

    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window2)

        // znalezienie elementów
        enterLastPeriod = findViewById(R.id.enterLastPeriod)
        cycleLen = findViewById(R.id.cycleLen)
        periodLen = findViewById(R.id.periodLen)
        weightRegister = findViewById(R.id.weightRegister)
        buttonConfirmRegisterWindow2 = findViewById(R.id.buttonConfirmRegisterWindow2)

        // Pobranie userId z intent
        val userId = intent.getStringExtra("USER_ID")

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięcia przycisku
        buttonConfirmRegisterWindow2.setOnClickListener {
            val lastPeriod = enterLastPeriod.text.toString()
            val cycleLength = cycleLen.text.toString()
            val periodLength = periodLen.text.toString()
            val weight = weightRegister.text.toString()

            if (lastPeriod.isNotEmpty() && cycleLength.isNotEmpty() && periodLength.isNotEmpty() && weight.isNotEmpty()) {
                // Utworzenie mapy z danymi użytkownika
                val userDetails = mapOf(
                    "lastPeriod" to lastPeriod,
                    "cycleLength" to cycleLength,
                    "periodLength" to periodLength,
                    "weight" to weight
                )

                // Uruchomienie korutyny w wątku głównym
                GlobalScope.launch(Dispatchers.Main) {
                    // Dodanie danych użytkownika do bazy danych Firestore
                    db.collection("users").document(userId!!)
                        .set(userDetails)
                        .addOnSuccessListener {
                            openRegisterWindow3Activity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@RegisterWindow2Activity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                // Wyświetlenie komunikatu o błędzie
                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openRegisterWindow3Activity() {
        val intent = Intent(this, RegisterWindow3Activity::class.java)
        startActivity(intent)
    }
}
