package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsWindowActivity : AppCompatActivity(){
    private lateinit var settingsWidnowSettingButton: ImageButton
    private lateinit var settingWindowAcountButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)

        settingsWidnowSettingButton = findViewById(R.id.settingsWidnowSettingButton)
        settingWindowAcountButton = findViewById(R.id.settingWindowAcountButton)


        settingWindowAcountButton.setOnClickListener {
            openSettingsWindowActivity()
        }

        settingsWidnowSettingButton.setOnClickListener {
            openAccountWindowActivity()
        }

    }
    private fun openSettingsWindowActivity() {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        startActivity(intent)
    }
    private fun openAccountWindowActivity(){
        val intent = Intent(this, AccountWindowActivity::class.java)
        startActivity(intent)
    }


}