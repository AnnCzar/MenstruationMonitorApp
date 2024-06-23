package com.example.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

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
        val email = intent.getStringExtra("EMAIL")
        val password = intent.getStringExtra("PASSWORD")
        val username = intent.getStringExtra("USERNAME")

        // Date Picker for Last Period
        enterLastPeriod.setOnClickListener {
            showDatePickerDialog()
        }

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięcia przycisku
        buttonConfirmRegisterWindow2.setOnClickListener {
            val lastPeriod = enterLastPeriod.text.toString()
            val cycleLength = cycleLen.text.toString()
            val periodLength = periodLen.text.toString()
            val weight = weightRegister.text.toString()

            if (lastPeriod.isNotEmpty() && cycleLength.toInt() > 0 && periodLength.toInt() > 0 && weight.toInt() > 0) {
                openRegisterWindow3Activity(userId!!, email!!, password!!, username!!, lastPeriod, cycleLength, periodLength, weight)
            } else {
                // Wyświetlenie komunikatu o błędzie
                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                enterLastPeriod.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun openRegisterWindow3Activity(
        userId: String, email: String, password: String, username: String,
        lastPeriod: String, cycleLength: String, periodLength: String, weight: String
    ) {
        val intent = Intent(this, RegisterWindow3Activity::class.java).apply {
            putExtra("USER_ID", userId)
            putExtra("EMAIL", email)
            putExtra("PASSWORD", password)
            putExtra("USERNAME", username)
            putExtra("LAST_PERIOD", lastPeriod)
            putExtra("CYCLE_LENGTH", cycleLength.toInt())
            putExtra("PERIOD_LENGTH", periodLength.toInt())
            putExtra("WEIGHT", weight.toDouble())
        }
        startActivity(intent)
    }
}
