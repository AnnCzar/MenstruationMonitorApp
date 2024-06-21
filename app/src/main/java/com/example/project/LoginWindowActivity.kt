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

        // Finding elements
        enterLogin = findViewById(R.id.enterLogin)
        enterPassword = findViewById(R.id.enterPassword)
        buttonConfirmLogin = findViewById(R.id.buttonConfirmLogin)

        // Set button click listener
        buttonConfirmLogin.setOnClickListener {
            logInRegisteredUser()
        }
    }

    // FirebaseFirestore reference for interacting with Firestore database
    val db = Firebase.firestore

    private fun validateLoginDetails(): Boolean {
        return when {
            // Check if login is empty
            TextUtils.isEmpty(enterLogin.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(getString(R.string.err_msg_enter_email), true)
                false
            }
            // Check if password is empty
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

    /**
     * Method to handle the login of a registered user.
     */
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val login = enterLogin.text.toString().trim { it <= ' ' }
            val password = enterPassword.text.toString().trim { it <= ' ' }

            // Logging in using Firebase Auth
            auth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Save user ID to SharedPreferences
                        val user = auth.currentUser
                        if (user != null) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                try {
                                    val userDocument = db.collection("users").document(user.uid).get().await()
                                    val statusPregnancy = userDocument.getBoolean("statusPregnancy")

                                    // Save user ID to SharedPreferences
                                    val sharedPreferences: SharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                                    with(sharedPreferences.edit()) {
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
        // Add logging
        println("Opening MainWindowPeriodActivity for userId: $userId")
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openMainWindowPregnancyActivity(userId: String) {
        // Add logging
        println("Opening MainWindowPregnancyActivity for userId: $userId")
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}


//package com.example.project
//
//import com.example.project.MainWindowPregnancyActivity
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.text.TextUtils
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//class LoginWindowActivity : AppCompatActivity() {
//    private lateinit var enterLogin: EditText
//    private lateinit var enterPassword: EditText
//    private lateinit var buttonConfirmLogin: Button
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.login_window)
//
//        auth = FirebaseAuth.getInstance()
//
//        // Finding elements
//        enterLogin = findViewById(R.id.enterLogin)
//        enterPassword = findViewById(R.id.enterPassword)
//        buttonConfirmLogin = findViewById(R.id.buttonConfirmLogin)
//
//        // Set button click listener
//        buttonConfirmLogin.setOnClickListener {
//            logInRegisteredUser()
//        }
//    }
//
//    // FirebaseFirestore reference for interacting with Firestore database
//    val db = Firebase.firestore
//
//    private fun validateLoginDetails(): Boolean {
//        return when {
//            // Check if login is empty
//            TextUtils.isEmpty(enterLogin.text.toString().trim { it <= ' ' }) -> {
//                showErrorSnackBar(getString(R.string.err_msg_enter_email), true)
//                false
//            }
//            // Check if password is empty
//            TextUtils.isEmpty(enterPassword.text.toString().trim { it <= ' ' }) -> {
//                showErrorSnackBar(getString(R.string.err_msg_enter_password), true)
//                false
//            }
//            else -> {
//                showErrorSnackBar("Your details are valid", false)
//                true
//            }
//        }
//    }
//
//    /**
//     * Method to handle the login of a registered user.
//     */
//    private fun logInRegisteredUser() {
//        if (validateLoginDetails()) {
//            val login = enterLogin.text.toString().trim { it <= ' ' }
//            val password = enterPassword.text.toString().trim { it <= ' ' }
//
//            // Logging in using Firebase Auth
//            auth.signInWithEmailAndPassword(login, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Save user ID to SharedPreferences
//                        val user = auth.currentUser
//                        if (user != null) {
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                try {
//                                    val userDocument = db.collection("users").document(user.uid).get().await()
//                                    val statusPregnancy = userDocument.getBoolean("statusPregnancy")
//
//                                    // Save user ID to SharedPreferences
//                                    val sharedPreferences: SharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
//                                    with(sharedPreferences.edit()) {
//                                        putString("USER_ID", user.uid)
//                                        apply()
//                                    }
//
//                                    if (statusPregnancy == true) {
//                                        openMainWindowPregnancyActivity(user.uid)
//                                    } else if (statusPregnancy == false) {
//                                        openMainWindowPeriodActivity(user.uid)
//                                    } else {
//                                        showErrorSnackBar("Nieznany status ciąży", true)
//                                    }
//                                } catch (e: Exception) {
//                                    showErrorSnackBar("Błąd: ${e.message}", true)
//                                }
//                            }
//                        } else {
//                            showErrorSnackBar("Błąd: Nie udało się uzyskać informacji o użytkowniku", true)
//                        }
//                    } else {
//                        showErrorSnackBar("Błąd logowania: ${task.exception?.message}", true)
//                    }
//                }
//        } else {
//            showErrorSnackBar("Pola nie mogą być puste", true)
//        }
//    }
//
//
//    private fun showErrorSnackBar(message: String, errorMessage: Boolean) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun openMainWindowPeriodActivity(userId: String) {
//        // Add logging
//        println("Opening MainWindowPeriodActivity for userId: $userId")
//        val intent = Intent(this, MainWindowPeriodActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//
//    private fun openMainWindowPregnancyActivity(userId: String) {
//        // Add logging
//        println("Opening MainWindowPregnancyActivity for userId: $userId")
//        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }
//}
