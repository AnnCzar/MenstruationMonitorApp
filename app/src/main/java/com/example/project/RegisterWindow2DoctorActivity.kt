package com.example.project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.collections.Doctor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterWindow2DoctorActivity : AppCompatActivity(){

    private lateinit var name: EditText
    private lateinit var confirmButton: Button
    private lateinit var specialisation: Spinner
    val db = Firebase.firestore



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window2_doctor)

        name = findViewById(R.id.name)
        confirmButton = findViewById(R.id.buttonConfirmRegisterWindow2Doctor)
        specialisation = findViewById(R.id.specialisation)

        val specificationAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.specialisation_array,
            android.R.layout.simple_spinner_item
        )
        specificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        specialisation.adapter = specificationAdapter


        val userId = intent.getStringExtra("USER_ID")
        val email = intent.getStringExtra("EMAIL")
        val password = intent.getStringExtra("PASSWORD")
        val username = intent.getStringExtra("USERNAME")
        val role = intent.getStringExtra("ROLE")

        confirmButton.setOnClickListener {
            val enteredName = name.text.toString()
            if (enteredName.isNotEmpty()){
                val selectedSpecification = specialisation.selectedItem.toString()
                if ((userId != null) && (email != null) && (password != null) && (username != null) && (role != null)) {
                    saveAllUserData(userId, email, password,username, role, selectedSpecification )
                    openMainWindowDoctor(userId)
                }
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAllUserData(
        userId: String, email: String, password: String, username: String, role: String, selectedSpecification: String

    ) {
        val user = Doctor(
            email = email,
            login = username,
            password = password,
            role = role,
            specialisation = selectedSpecification
        )

        GlobalScope.launch(Dispatchers.Main) {
            db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this@RegisterWindow2DoctorActivity, "Dane użytkownika zapisane", Toast.LENGTH_SHORT).show()
                    openMainWindowDoctor(userId)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@RegisterWindow2DoctorActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openMainWindowDoctor(userId: String){
        val intent = Intent(this, MainWindowDoctor::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}