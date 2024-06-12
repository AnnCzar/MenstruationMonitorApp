package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.collections.Cycles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginWindowActivity : AppCompatActivity() {
    private lateinit var enterLogin: EditText
    private lateinit var enterPassword: EditText
    private lateinit var buttonConfirmLogin: Button

    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_window)

        // znalezienie elementów
        enterLogin = findViewById(R.id.enterLogin)
        enterPassword = findViewById(R.id.enterPassword)
        buttonConfirmLogin = findViewById(R.id.buttonConfirmLogin)

        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięcia przycisku
        buttonConfirmLogin.setOnClickListener {
            val login = enterLogin.text.toString()
            val password = enterPassword.text.toString()

            if (login.isNotEmpty() && password.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val user = db.collection("users")
                            .whereEqualTo("login", login)
                            .get()
                            .await()
                            .documents
                            .firstOrNull()

                        if (user != null) {
                            val storedPassword = user.getString("password")
                            if (storedPassword == password) {
                                if (user.getBoolean("statusPregnancy") == false){
                                    openMainWindowPeriodActivity(user.id)
                                }
                                else if (user.getBoolean("statusPregnancy") == true){
                                    openMainWindowPregnancyActivity(user.id)
                                }

                            } else {
                                Toast.makeText(this@LoginWindowActivity, "Nieprawidłowe hasło", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginWindowActivity, "Użytkownik nie istnieje", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginWindowActivity, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
