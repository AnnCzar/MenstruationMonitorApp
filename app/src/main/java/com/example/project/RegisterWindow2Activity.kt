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

    // Obiekt do obsługi operacji na bazie danych Firestore
    private val dbOperations = FirestoreDatabaseOperations(db)

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
            val cycleLength = cycleLen.text.toString().toInt()
            val periodLength = periodLen.text.toString().toInt()
            val weight = weightRegister.text.toString().toDouble()

            if (lastPeriod.isNotEmpty() && cycleLength.toString().isNotEmpty() && periodLength.toString().isNotEmpty() && weight.toString().isNotEmpty()) {
                // Konwersja lastPeriod do Date
                val lastPeriodDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(lastPeriod)

                // Pobierz aktualnego użytkownika z Firestore
                GlobalScope.launch(Dispatchers.Main) {
                    val user = dbOperations.getUser(userId!!)
                    if (user != null) {
                        // Aktualizacja danych użytkownika
                        user.lastPeriodDate = lastPeriodDate
                        user.cycleLength = cycleLength
                        user.periodLength = periodLength
                        user.weight = weight
                        // Zaktualizuj użytkownika w bazie danych Firestore
                        dbOperations.updateUser(userId, user)
                        openRegisterWindow3Activity()
                    } else {
                        Toast.makeText(this@RegisterWindow2Activity, "Błąd: Użytkownik nie znaleziony", Toast.LENGTH_SHORT).show()
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
