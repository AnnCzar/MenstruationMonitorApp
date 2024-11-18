package com.example.project

import com.example.project.MainWindowPregnancyActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SettingsWindowActivity : AppCompatActivity(){
    private lateinit var homeButtonSetting: ImageButton
    private lateinit var settingWindowAcountButton: ImageButton
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var changePasswordButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)


        homeButtonSetting = findViewById(R.id.homeButtonSetting)
        settingWindowAcountButton = findViewById(R.id.settingWindowAcountButton)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        changePasswordButton.setOnClickListener{
            openChangePassword(userId)

        }


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
                            // Obsługa przypadku, gdy statusPregnancy nie został ustawiony lub jest null
                        }
                    } else {
                        // Obsługa przypadku, gdy użytkownik nie istnieje
                    }
                }
                .addOnFailureListener { e ->
                    // Obsługa błędów podczas pobierania danych użytkownika
                    Toast.makeText(this@SettingsWindowActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


    }
    private fun openChangePassword(userId: String) {
        val intent = Intent(this, ChangePassword::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun openAccountWindowActivity(userId: String){
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }


}