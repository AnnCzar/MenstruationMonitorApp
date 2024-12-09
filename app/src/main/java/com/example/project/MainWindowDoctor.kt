package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle

import android.util.Log


import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainWindowDoctor : AppCompatActivity(){

    private lateinit var settingButton: ImageButton
    private lateinit var acountButton: ImageButton
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_doctor)


        userId = intent.getStringExtra("USER_ID") ?: ""
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@MainWindowDoctor, "Cofanie jest wyłączone!", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun openSettingsWindowActivity(userId: String) {

    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Toast.makeText(this, "Cofanie jest wyłączone!", Toast.LENGTH_SHORT).show()
    }


    private fun openAccountWindowActivity(userId: String) {
        val intent = Intent(this, AccountWindowDoctorActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }


}