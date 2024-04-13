package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AccountWindowActivity : AppCompatActivity(){
    private lateinit var accountWidnowSettingButton: ImageButton
//    private lateinit var accountWindowAcountButton: ImageButton
    private lateinit var homeButtonProfil : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_window)

        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        homeButtonProfil = findViewById(R.id.homeButtonSetting)

        // dodac przechodzenie do main

//        accountWindowAcountButton.setOnClickListener {
//            openAccountWindowActivity()
//        }

        accountWidnowSettingButton.setOnClickListener {
            openSettingsWindowActivity()
        }

    }
    private fun openSettingsWindowActivity() {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        startActivity(intent)
    }

//    private fun openAccountWindowActivity(){
//        val intent = Intent(this, AccountWindowActivity::class.java)
//        startActivity(intent)
//    }



}