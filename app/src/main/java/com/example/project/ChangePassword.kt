package com.example.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Activity for changing the user's password.
 * This activity allows users to input their old password, new password, and confirm the new password.
 * Upon validation, the password is changed, and the user is logged out.
 */
class ChangePassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    private lateinit var enterOldPassword: EditText
    private lateinit var enterNewPassword: EditText
    private lateinit var repeatNewPassword: EditText
    private lateinit var buttonConfirmNewPassword: Button

    /**
     * Called when the activity is created. Initializes the views and sets up the button click listener.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)

        auth = FirebaseAuth.getInstance()

        // znalezienie elementów
        enterOldPassword = findViewById(R.id.enterOldPassword)
        enterNewPassword = findViewById(R.id.enterChangedPassword)
        repeatNewPassword = findViewById(R.id.repeatChangedPassword)
        buttonConfirmNewPassword = findViewById(R.id.buttonConfirmNewPassword)

        buttonConfirmNewPassword.setOnClickListener {
            val newPassword = enterNewPassword.text.toString()
            val repeatedPassword = repeatNewPassword.text.toString()
            val oldPassword = enterOldPassword.text.toString()

            if (newPassword.isNotEmpty() && oldPassword.isNotEmpty() && repeatedPassword.isNotEmpty()) {
                if (newPassword == repeatedPassword) {
                    GlobalScope.launch(Dispatchers.Main) {
                        changeUserPassword(newPassword, oldPassword)
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
     * Changes the user's password after re-authenticating with their old password.
     * This method performs re-authentication and updates the password if successful.
     *
     * @param newPassword The new password to be set.
     * @param oldPassword The current password to be validated.
     */
    private fun changeUserPassword(newPassword: String, oldPassword: String) {
        val user = auth.currentUser

        if (user != null) {
            val email = user.email
            if (email != null) {
                // Tworzenie credential z email i hasła
                val credential = EmailAuthProvider.getCredential(email, oldPassword)

                // Ponowna autentykacja
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            // Po pomyślnej autentykacji, zmiana hasła
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Hasło zostało zmienione",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        logout()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Błąd zmiany hasła: ${updateTask.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Błąd autentykacji: ${reauthTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Brak adresu email użytkownika", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Użytkownik nie jest zalogowany", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Logs out the user and redirects to the login screen.
     * Clears login-related preferences and signs out the Firebase user.
     */
    private fun logout() {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            putString("USER_ID", null)
            apply()
        }
        auth.signOut()

        val intent = Intent(this, LoginWindowActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
