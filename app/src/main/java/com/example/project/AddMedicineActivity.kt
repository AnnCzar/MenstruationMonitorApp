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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        buttonConfirmRegisterWindow3 = findViewById(R.id.buttonConfirmRegisterWindow3)
        buttonSaveMedicineRegisterWindow3 = findViewById(R.id.buttonSaveMedicineRegisterWindow3)
        buttonConfirmRegisterWindow3.visibility = Button.GONE


        val userId = intent.getStringExtra("USER_ID")


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
    }
}
