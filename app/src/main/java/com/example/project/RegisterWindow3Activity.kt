package com.example.project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.collections.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class RegisterWindow3Activity : AppCompatActivity() {
    private lateinit var enterMedicineName: EditText
    private lateinit var enterDoseMedicineRegister: EditText
    private lateinit var enterTimeMedicineRegister: EditText
    private lateinit var buttonConfirmRegisterWindow3: Button
    private lateinit var buttonSaveMedicineRegisterWindow3: Button

    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = Firebase.firestore

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.register_window3)
//
//        // znalezienie elementów
//        enterMedicineName = findViewById(R.id.enterMedicineName)
//        enterDoseMedicineRegister = findViewById(R.id.enterDoseMedicineRegister)
//        enterTimeMedicineRegister = findViewById(R.id.enterTimeMedicineRegister)
//        buttonConfirmRegisterWindow3 = findViewById(R.id.buttonConfirmRegisterWindow3)
//        buttonSaveMedicineRegisterWindow3 = findViewById(R.id.buttonSaveMedicineRegisterWindow3)
//
//        // Pobranie userId z intent
//        val userId = intent.getStringExtra("USER_ID")
//        val email = intent.getStringExtra("EMAIL")
//        val password = intent.getStringExtra("PASSWORD")
//        val username = intent.getStringExtra("USERNAME")
//        val lastPeriod1 = intent.getStringExtra("LAST_PERIOD")
//        val cycleLength = intent.getIntExtra("CYCLE_LENGTH", 0)
//        val periodLength = intent.getIntExtra("PERIOD_LENGTH", 0)
//        val weight = intent.getDoubleExtra("WEIGHT", 0.0)
//
//
//        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val lastPeriod: LocalDate?
//        if (!lastPeriod1.isNullOrBlank()) {
//            lastPeriod =LocalDate.parse(lastPeriod1, dateFormatter)
//        } else {
//            lastPeriod = LocalDate.of(2023, Month.JANUARY, 1)
//        }
//        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięcia przycisku
//        buttonSaveMedicineRegisterWindow3.setOnClickListener {
//            val medicineName = enterMedicineName.text.toString()
//            val doseMedicine = enterDoseMedicineRegister.text.toString()
//            val timeMedicine = enterTimeMedicineRegister.text.toString()
//
//            if (medicineName.isNotEmpty() && doseMedicine.isNotEmpty() && timeMedicine.isNotEmpty()) {
//                // Utworzenie mapy z danymi dotyczącymi leku
//                val medicineDetails = mapOf(
//                    "medicineName" to medicineName,
//                    "doseMedicine" to doseMedicine,
//                    "timeMedicine" to timeMedicine
//                )
//
//                // Uruchomienie korutyny w wątku głównym
//                GlobalScope.launch(Dispatchers.Main) {
//                    // Dodanie danych dotyczących leku do bazy danych Firestore
//                    db.collection("users").document(userId!!)
//                        .collection("medicines").add(medicineDetails)
//                        .addOnSuccessListener {
//                            Toast.makeText(this@RegisterWindow3Activity, "Lek zapisany", Toast.LENGTH_SHORT).show()
//                        }
//                        .addOnFailureListener { e ->
//                            Toast.makeText(this@RegisterWindow3Activity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                }
//            } else {
//                // Wyświetlenie komunikatu o błędzie
//                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        buttonConfirmRegisterWindow3.setOnClickListener {
//            saveAllUserData(userId!!, email!!, password!!, username!!, lastPeriod!!, cycleLength, periodLength, weight)
//            openMainWindowPeriodActivity()
//        }
//    }
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun saveAllUserData(
//        userId: String, email: String, password: String, username: String,
//        lastPeriod: LocalDate?, cycleLength: Int, periodLength: Int, weight: Double
//    ) {
//        val userDetails = mutableMapOf(
//            "email" to email,
//            "login" to username,
//            "password" to password,
//            "cycleLength" to cycleLength,
//            "lastPeriodDate" to lastPeriod,
//            "periodLength" to periodLength,
//            "statusPregnancy" to false,
//            "weight" to weight
//        )
//        // Dodaj lastPeriod tylko jeśli nie jest null
//        lastPeriod?.let {
//            userDetails["lastPeriodDate"] = it
//        }
//
//        GlobalScope.launch(Dispatchers.Main) {
//            db.collection("users").document(userId)
//                .set(userDetails)
//                .addOnSuccessListener {
//                    openMainWindowPeriodActivity()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this@RegisterWindow3Activity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    private fun openMainWindowPeriodActivity() {
//        val intent = Intent(this, MainWindowPeriodActivity::class.java)
//        startActivity(intent)
//    }
//}



    @RequiresApi(Build.VERSION_CODES.O)
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
        val email = intent.getStringExtra("EMAIL")
        val password = intent.getStringExtra("PASSWORD")
        val username = intent.getStringExtra("USERNAME")
        val lastPeriod1 = intent.getStringExtra("LAST_PERIOD")
        val cycleLength = intent.getIntExtra("CYCLE_LENGTH", 0)
        val periodLength = intent.getIntExtra("PERIOD_LENGTH", 0)
        val weight = intent.getDoubleExtra("WEIGHT", 0.0)

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val lastPeriodDate: LocalDate? = if (!lastPeriod1.isNullOrBlank()) {
            LocalDate.parse(lastPeriod1, dateFormatter)
        } else {
            LocalDate.of(2023, Month.JANUARY, 1)
        }

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięcia przycisku
        buttonSaveMedicineRegisterWindow3.setOnClickListener {
            val medicineName = enterMedicineName.text.toString()
            val doseMedicine = enterDoseMedicineRegister.text.toString()
            val timeMedicine = enterTimeMedicineRegister.text.toString()

            if (medicineName.isNotEmpty() && doseMedicine.isNotEmpty() && timeMedicine.isNotEmpty()) {
                // Utworzenie obiektu użytkownika
                val user = Users(email = email!!, login = username!!, password = password!!,
                    cycleLength = cycleLength, lastPeriodDate = lastPeriodDate,
                    periodLength = periodLength, weight = weight)

                // Uruchomienie korutyny w wątku głównym
                GlobalScope.launch(Dispatchers.Main) {
                    // Dodanie danych użytkownika do bazy danych Firestore
                    db.collection("Users").document(userId!!)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this@RegisterWindow3Activity, "Dane użytkownika zapisane", Toast.LENGTH_SHORT).show()
                            // Możesz dodać tutaj wywołanie funkcji, która przechodzi do następnego okna
                            openMainWindowPeriodActivity()
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
            // Możesz dodać tutaj wywołanie funkcji, która przechodzi do następnego okna
            openMainWindowPeriodActivity()
        }
    }

    private fun openMainWindowPeriodActivity() {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        startActivity(intent)
    }
}
