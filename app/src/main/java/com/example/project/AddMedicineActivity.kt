package com.example.project

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

class AddMedicineActivity : AppCompatActivity(){

    private lateinit var enterMedicineName: EditText
    private lateinit var enterDoseMedicineRegister: EditText
    private lateinit var enterTimeMedicineRegister: EditText
    private lateinit var buttonConfirmRegisterWindow3: Button
    private lateinit var buttonSaveMedicineRegisterWindow3: Button

    val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window3)

        enterMedicineName = findViewById(R.id.enterMedicineName)
        enterDoseMedicineRegister = findViewById(R.id.enterDoseMedicineRegister)
        enterTimeMedicineRegister = findViewById(R.id.enterTimeMedicineRegister)
//        buttonConfirmRegisterWindow3 = findViewById(R.id.buttonConfirmRegisterWindow3)
        buttonSaveMedicineRegisterWindow3 = findViewById(R.id.buttonSaveMedicineRegisterWindow3)

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

        buttonSaveMedicineRegisterWindow3.setOnClickListener {
            val medicineName = enterMedicineName.text.toString()
            val doseMedicine = enterDoseMedicineRegister.text.toString()
            val timeMedicine = enterTimeMedicineRegister.text.toString()

            if (medicineName.isNotEmpty() && doseMedicine.isNotEmpty() && timeMedicine.isNotEmpty()) {
                val medicineDetails = mapOf(
                    "medicineName" to medicineName,
                    "doseMedicine" to doseMedicine,
                    "timeMedicine" to timeMedicine
                )

                GlobalScope.launch(Dispatchers.Main) {
                    db.collection("users").document(userId!!)
                        .collection("medicines").add(medicineDetails)
                        .addOnSuccessListener {
                            Toast.makeText(this@AddMedicineActivity, "Lek zapisany", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@AddMedicineActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
            }
        }

        buttonConfirmRegisterWindow3.setOnClickListener {
            saveAllUserData(userId!!, email!!, password!!, username!!, lastPeriodDate, cycleLength, periodLength, weight)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAllUserData(
        userId: String, email: String, password: String, username: String,
        lastPeriod: LocalDate?, cycleLength: Int, periodLength: Int, weight: Double
    ) {
        val user = Users(
            email = email,
            login = username,
            password = password,
            cycleLength = cycleLength,
            lastPeriodDate = lastPeriod,
            periodLength = periodLength,
            weight = weight
        )

        GlobalScope.launch(Dispatchers.Main) {
            db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this@AddMedicineActivity, "Dane użytkownika zapisane", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@AddMedicineActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
