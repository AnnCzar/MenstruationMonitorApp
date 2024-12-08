package com.example.project

import android.content.Intent
import android.os.Bundle

import android.util.Log


import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainWindowDoctor : AppCompatActivity(){

    private lateinit var settingButton: ImageButton
    private lateinit var acountButton: ImageButton
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_doctor)


        userId = intent.getStringExtra("USER_ID") ?: ""

    }

    private fun openSettingsWindowActivity(userId: String) {

    }

    private fun openAccountWindowActivity(userId: String) {
        val intent = Intent(this, AccountWindowDoctorActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

}