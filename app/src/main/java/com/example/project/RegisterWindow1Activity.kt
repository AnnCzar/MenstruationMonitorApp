package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.FirestoreDatabaseOperations
import database.collections.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RegisterWindow1Activity : AppCompatActivity() {

    private lateinit var enterEmailRegister: EditText
    private lateinit var enterPasswordRegister: EditText
    private lateinit var enterPasswordRegisterConfirm: EditText
    private lateinit var enterUsernameRegister: EditText
    private lateinit var buttonConfirmRegisterWindow1: Button


    private lateinit var userId: String

    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = Firebase.firestore

    // Obiekt do obsługi operacji na bazie danych Firestore
    private val dbOperations = FirestoreDatabaseOperations(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window1)

        // znalezienie elementów
        enterEmailRegister = findViewById(R.id.enterEmailRegister)
        enterPasswordRegister = findViewById(R.id.enterPasswordRegister)
        enterPasswordRegisterConfirm = findViewById(R.id.enterPasswordRegisterConfirm)
        enterUsernameRegister = findViewById(R.id.enterUsernameRegister)
        buttonConfirmRegisterWindow1 = findViewById(R.id.buttonConfirmRegisterWindow1)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięcia przycisku
        buttonConfirmRegisterWindow1.setOnClickListener {
            // Pobranie danych użytkownika z pól tekstowych
            val email = enterEmailRegister.text.toString()
            val password = enterPasswordRegister.text.toString()
            val passwordConfirm = enterPasswordRegisterConfirm.text.toString()
            val username = enterUsernameRegister.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty() && username.isNotEmpty()) {
                if (password == passwordConfirm) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val emailExists = checkIfEmailExists(email)
                        if (!emailExists) {
                            // Generowanie unikalnego userId
                            val userId = UUID.randomUUID().toString()
                            // Utworzenie obiektu Users
                            val user = Users(email, username, password)
                            openRegisterWindow2Activity(userId, email, password, username)
                        } else {
                            Toast.makeText(
                                this@RegisterWindow1Activity,
                                "Email już jest zajęty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Wyświetlenie komunikatu o błędzie
                    Toast.makeText(this, "Hasła nie są zgodne", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Wyświetlenie komunikatu o błędzie
                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun checkIfEmailExists(email: String): Boolean {
        return try {
            val result = db.collection("users").whereEqualTo("email", email).get().await()
            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    private fun openRegisterWindow2Activity(
        userId: String,
        email: String,
        password: String,
        username: String
    ) {
        val intent = Intent(this, RegisterWindow2Activity::class.java).apply {
            putExtra("USER_ID", userId)
            putExtra("EMAIL", email)
            putExtra("PASSWORD", password)
            putExtra("USERNAME", username)
        }
        startActivity(intent)
    }
}
