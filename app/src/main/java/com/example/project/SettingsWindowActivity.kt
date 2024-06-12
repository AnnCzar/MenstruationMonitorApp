package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SettingsWindowActivity : AppCompatActivity(){
    private lateinit var homeButtonSetting: ImageButton
    private lateinit var settingWindowAcountButton: ImageButton
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)


        homeButtonSetting = findViewById(R.id.homeButtonSetting)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()


        settingWindowAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

//        homeButtonSetting.setOnClickListener {
//        intent.putExtra("USER_ID", userId)
//            openAccountMain()
//        }

    }
//    private fun openSettingsWindowActivity(userId: String) {
//            intent.putExtra("USER_ID", userId)
//        val intent = Intent(this, SettingsWindowActivity::class.java)
//        startActivity(intent)
//    }
    private fun openAccountWindowActivity(userId: String){
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }


}