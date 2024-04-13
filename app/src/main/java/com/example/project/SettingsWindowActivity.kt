package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsWindowActivity : AppCompatActivity(){
    private lateinit var homeButtonSetting: ImageButton
    private lateinit var settingWindowAcountButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)


        homeButtonSetting = findViewById(R.id.homeButtonSetting)
        settingWindowAcountButton = findViewById(R.id.settingWindowAcountButton)


        settingWindowAcountButton.setOnClickListener {
            openAccountWindowActivity()
        }

//        homeButtonSetting.setOnClickListener {
//            openAccountMain()
//        }

    }
//    private fun openSettingsWindowActivity() {
//        val intent = Intent(this, SettingsWindowActivity::class.java)
//        startActivity(intent)
//    }
    private fun openAccountWindowActivity(){
        val intent = Intent(this, AccountWindowActivity::class.java)
        startActivity(intent)
    }


}