package com.example.project
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.*
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.FirestoreDatabaseOperations
import database.collections.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID


/**
 * RegisterWindow1Activity is the first registration screen where users enter their email, password, username, and role.
 * It handles user registration, including email verification and role assignment.
 */
class RegisterWindow1Activity : AppCompatActivity() {

    private lateinit var enterEmailRegister: EditText
    private lateinit var enterPasswordRegister: EditText
    private lateinit var enterPasswordRegisterConfirm: EditText
    private lateinit var enterUsernameRegister: EditText
    private lateinit var buttonConfirmRegisterWindow1: Button
    private lateinit var roleSpinner: Spinner

    private lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_window1)

        auth = FirebaseAuth.getInstance()

        // znalezienie elementów
        enterEmailRegister = findViewById(R.id.enterEmailRegister)
        enterPasswordRegister = findViewById(R.id.enterPasswordRegister)
        enterPasswordRegisterConfirm = findViewById(R.id.enterPasswordRegisterConfirm)
        enterUsernameRegister = findViewById(R.id.enterUsernameRegister)
        buttonConfirmRegisterWindow1 = findViewById(R.id.buttonConfirmRegisterWindow1)
        roleSpinner = findViewById(R.id.role)


        val roleAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.role_array,
            R.layout.spinner_item
        )
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = roleAdapter

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
                            registerUser(email, password, username)
                        } else {
                            Toast.makeText(
                                this@RegisterWindow1Activity,
                                "Email już jest zajęty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Hasła nie są zgodne", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Pola nie mogą być puste", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Checks if the given email already exists in the Firestore database.
     * @param email The email to check.
     * @return `true` if the email exists, `false` otherwise.
     */
    private suspend fun checkIfEmailExists(email: String): Boolean {
        return try {
            val result = db.collection("users").whereEqualTo("email", email).get().await()
            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Registers a new user with the given email, password, and username.
     * If successful, it proceeds to the appropriate next screen based on the selected role.
     * @param email The email address of the new user.
     * @param password The password for the new user.
     * @param username The username for the new user.
     */
    private fun registerUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid


                    if (userId != null) {
                        val selectedRole = roleSpinner.selectedItem.toString()
                        openRegisterWindow2Activity(userId, email, password, username, selectedRole)
                    }
                } else {
                    Toast.makeText(this, "Rejestracja nieudana: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Opens the appropriate next registration screen based on the selected role.
     * @param userId The unique ID of the user.
     * @param email The email address of the user.
     * @param password The password for the user.
     * @param username The username of the user.
     * @param role The selected role of the user (e.g., "Lekarz" or "Zwykły użytkownik").
     */
    private fun openRegisterWindow2Activity(
        userId: String,
        email: String,
        password: String,
        username: String,
        role: String
    ) {

        val intent = when (role) {
            "Lekarz" -> Intent(this, RegisterWindow2DoctorActivity::class.java)
            "Zwykły użytkownik" -> Intent(this, RegisterWindow2Activity::class.java)
            else -> {
                Toast.makeText(this, "Nieprawidłowa rola: $role", Toast.LENGTH_SHORT).show()
                return
            }
        }
        intent.apply {
            putExtra("USER_ID", userId)
            putExtra("EMAIL", email)
            putExtra("PASSWORD", password)
            putExtra("USERNAME", username)
            putExtra("ROLE", role)
        }
        startActivity(intent)
    }
}