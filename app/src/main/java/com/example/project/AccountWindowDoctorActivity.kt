package com.example.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountWindowDoctorActivity : AppCompatActivity(){

    private lateinit var accountWidnowSettingButton: ImageButton
    private lateinit var homeButtonProfil: ImageButton
    private lateinit var usernameTextView: TextView

    private lateinit var logoutButton: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_account_window)
        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()


        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logout()

        }
        auth = FirebaseAuth.getInstance()


    }

    private fun initializeViews() {
        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        homeButtonProfil = findViewById(R.id.homeButtonProfil)
        usernameTextView = findViewById(R.id.usernameTextView)

    }
}