package com.example.project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginWindowActivity : AppCompatActivity() {
    private lateinit var enterLogin: EditText
    private lateinit var enterPassword: EditText
    private lateinit var buttonConfirmLogin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_window)

        auth = FirebaseAuth.getInstance()

        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = sharedPreferences.getString("USER_ID", null)

        if (isLoggedIn && userId != null) {
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.uid == userId) {
                lifecycleScope.launch {
                    try {
                        val userDocument = db.collection("users").document(userId).get().await()
                        val statusPregnancy = userDocument.getBoolean("statusPregnancy")

                        if (statusPregnancy == true) {
                            openMainWindowPregnancyActivity(currentUser.uid)
                        } else {
                            openMainWindowPeriodActivity(currentUser.uid)
                        }
                        finish()
                    } catch (e: Exception) {
                        showErrorSnackBar("Błąd: ${e.message}", true)
                    }
                }
            }
        }


        enterLogin = findViewById(R.id.enterLogin)
        enterPassword = findViewById(R.id.enterPassword)
        buttonConfirmLogin = findViewById(R.id.buttonConfirmLogin)

        buttonConfirmLogin.setOnClickListener {
            logInRegisteredUser()
        }
    }

    val db = Firebase.firestore

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(enterLogin.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(enterPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid", false)
                true
            }
        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val login = enterLogin.text.toString().trim { it <= ' ' }
            val password = enterPassword.text.toString().trim { it <= ' ' }

            auth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                try {
                                    val userDocument = db.collection("users").document(user.uid).get().await()
                                    val statusPregnancy = userDocument.getBoolean("statusPregnancy")

                                    val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                                    with(sharedPreferences.edit()) {
                                        putBoolean("isLoggedIn", true)
                                        putString("USER_ID", user.uid)
                                        apply()
                                    }

                                    if (statusPregnancy == true) {
                                        openMainWindowPregnancyActivity(user.uid)
                                    } else if (statusPregnancy == false) {
                                        openMainWindowPeriodActivity(user.uid)
                                    } else {
                                        showErrorSnackBar("Nieznany status ciąży", true)
                                    }
                                } catch (e: Exception) {
                                    showErrorSnackBar("Błąd: ${e.message}", true)
                                }
                            }
                        } else {
                            showErrorSnackBar("Błąd: Nie udało się uzyskać informacji o użytkowniku", true)
                        }
                    } else {
                        showErrorSnackBar("Błąd logowania: ${task.exception?.message}", true)
                    }
                }
        } else {
            showErrorSnackBar("Pola nie mogą być puste", true)
        }
    }



    private fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openMainWindowPeriodActivity(userId: String) {

        println("Opening MainWindowPeriodActivity for userId: $userId")
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openMainWindowPregnancyActivity(userId: String) {

        println("Opening MainWindowPregnancyActivity for userId: $userId")
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
