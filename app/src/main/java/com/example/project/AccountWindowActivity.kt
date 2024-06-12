package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AccountWindowActivity : AppCompatActivity(){
    private lateinit var accountWidnowSettingButton: ImageButton
//    private lateinit var accountWindowAcountButton: ImageButton
    private lateinit var homeButtonProfil : ImageButton

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_window)

        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        homeButtonProfil = findViewById(R.id.homeButtonProfil)


        userId = intent.getStringExtra("USER_ID") ?: ""

        db = FirebaseFirestore.getInstance()

        // dodac przechodzenie do main

//        accountWindowAcountButton.setOnClickListener {
//            openAccountWindowActivity()
//        }

        accountWidnowSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

    }
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

//    private fun openAccountWindowActivity(userId: String){
//        val intent = Intent(this, AccountWindowActivity::class.java)
//    intent.putExtra("USER_ID", userId)
//        startActivity(intent)
//    }



}