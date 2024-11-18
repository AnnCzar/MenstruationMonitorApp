package com.example.project

import com.example.project.MainWindowPregnancyActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SettingsWindowActivity : AppCompatActivity(){
    private lateinit var homeButtonSetting: ImageButton
    private lateinit var settingWindowAcountButton: ImageButton
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)


        homeButtonSetting = findViewById(R.id.homeButtonSetting)
        settingWindowAcountButton = findViewById(R.id.settingWindowAcountButton)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()


        settingWindowAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }

        homeButtonSetting.setOnClickListener {
            val userRef = db.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { user ->
                    if (user != null) {
                        val statusPregnancy = user.getBoolean("statusPregnancy")
                        if (statusPregnancy != null) {
                            if (!statusPregnancy) {
                                openMainWindowPeriodActivity(userId)
                            } else {
                                openMainWindowPregnancyActivity(userId)
                            }
                        } else {

                        }
                    } else {
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@SettingsWindowActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }
    private fun openAccountWindowActivity(userId: String){
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }


}