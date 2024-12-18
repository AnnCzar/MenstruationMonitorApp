package com.example.project

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginWindowActivity : AppCompatActivity() {
    private lateinit var enterLogin: EditText
    private lateinit var enterPassword: EditText
    private lateinit var buttonConfirmLogin: Button
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val REQUEST_CODE_NOTIFICATIONS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_window)

        auth = FirebaseAuth.getInstance()

        checkAndRequestPostNotificationPermission()

        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userId = sharedPreferences.getString("USER_ID", null)
        // Zapisz token FCM do Firestore
        auth.uid?.let { saveFCMToken(it) }
        if (isLoggedIn && userId != null) {
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.uid == userId) {
                lifecycleScope.launch {
                    try {
                        val userDocument = db.collection("users").document(userId).get().await()
                        val role = userDocument.getString("role")
                        val statusPregnancy = userDocument.getBoolean("statusPregnancy")
                        if (role == "Lekarz") {
                            Log.d("dupa", "dupa")
                            openMainWindowDoctor(userId)
                        }else if (role == "Zwykły użytkownik"){
                            if (statusPregnancy == true) {
                                openMainWindowPregnancyActivity(currentUser.uid)
                            } else {
                                openMainWindowPeriodActivity(currentUser.uid)
                            }
                        } else{
                            showErrorSnackBar("Nieznana rola użytkownika", true)
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
    private fun checkAndRequestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Uprawnienie POST_NOTIFICATIONS zostało przyznane.")
            } else {
                Log.e("Permissions", "Uprawnienie POST_NOTIFICATIONS zostało odrzucone.")
                Toast.makeText(this, "Nie udzielono zgody na powiadomienia.", Toast.LENGTH_SHORT).show()
            }
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
                                    val role = userDocument.getString("role")
                                    val statusPregnancy = userDocument.getBoolean("statusPregnancy")

                                    // Zapisanie tokena FCM
                                    saveFCMToken(user.uid)


                                    val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                                    with(sharedPreferences.edit()) {
                                        putBoolean("isLoggedIn", true)
                                        putString("USER_ID", user.uid)
                                        apply()
                                    }
                                    Log.d("logowanie", role.toString())
                                    when (role) {

                                        "Lekarz" -> openMainWindowDoctor(user.uid)
                                        "Zwykły użytkownik" -> {
                                            if (statusPregnancy == true) {
                                                openMainWindowPregnancyActivity(user.uid)
                                            } else if (statusPregnancy == false) {
                                                openMainWindowPeriodActivity(user.uid)
                                            } else {
                                                showErrorSnackBar("Nieznany status ciąży", true)
                                            }
                                        }
                                        else -> showErrorSnackBar("Nieznana rola użytkownika", true)
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
    private fun saveFCMToken(userId: String) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Uzyskaj token
                val token = task.result
                Log.d("FCM", "Pobrano token: $token")
                if (token != null) {
                    val tokenData = mapOf("token" to token)

                    db.collection("Tokens").document(userId)
                        .set(tokenData)
                        .addOnSuccessListener {
                            Log.d("FCM", "Token zapisany w Firestore dla użytkownika: $userId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FCM", "Nie udało się zapisać tokena: ${e.message}")
                        }
                }
            }
    }





    private fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    //NAWIGACJA
    private fun openMainWindowDoctor(uid: String) {
        val intent = Intent(this, ChatDoctorActivity::class.java)
        intent.putExtra("USER_ID", uid)
        startActivity(intent)
        finish()
    }

    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }

    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }
}