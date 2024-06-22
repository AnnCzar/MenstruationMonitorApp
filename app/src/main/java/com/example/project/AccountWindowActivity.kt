
package com.example.project


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class AccountWindowActivity : AppCompatActivity() {
    private lateinit var accountWidnowSettingButton: ImageButton
    private lateinit var homeButtonProfil : ImageButton
    private lateinit var usernameTextView: TextView
    private lateinit var lastWeightTextView: TextView
    private lateinit var visitsButton: Button
    private lateinit var medicationsButton: Button
//    private lateinit var chartWeightTemperature: LineChart

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_window)

        initializeViews()


        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        loadUserInfo()
//        loadChartData()

        accountWidnowSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
        visitsButton.setOnClickListener {
            openVisitsWindow(userId)
        }

//        medicationsButton.setOnClickListener {
//
//        }

        homeButtonProfil.setOnClickListener {
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
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun initializeViews() {
        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        homeButtonProfil = findViewById(R.id.homeButtonProfil)
        usernameTextView = findViewById(R.id.usernameTextView)
        lastWeightTextView = findViewById(R.id.lastWeightTextView)
        visitsButton = findViewById(R.id.visitsButton)
        medicationsButton = findViewById(R.id.medicationsButton)

        medicationsButton.setOnClickListener {
            openMedicineWindowActivity(userId)
        }
//        chartWeightTemperature = findViewById(R.id.chartWeightTemperature)
    }

    private fun loadUserInfo() {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("login") ?: "N/A"
                    usernameTextView.text = username
                    Log.d("Firestore", "User login: $username")

                    val userWeight = document.getDouble("weight") ?: 0.0

                    db.collection("users").document(userId)
                        .collection("dailyInfo")
                        .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val lastWeight = documents.documents[0].getDouble("weight") ?: 0.0
                                lastWeightTextView.text = "Last weight: $lastWeight"
                                Log.d("Firestore", "Last weight from dailyInfo: $lastWeight")
                            } else {
                                // Jeśli `dailyInfo` jest puste, użyj wagi z `users`
                                lastWeightTextView.text = "Weight: $userWeight"
                                Log.d("Firestore", "No dailyInfo data found, using user weight: $userWeight")
                            }
                        }
                        .addOnFailureListener { e ->
                            lastWeightTextView.text = "Weight: $userWeight"
                            Log.e("Firestore", "Error fetching dailyInfo data, using user weight: $userWeight", e)
                        }
                } else {
                    usernameTextView.text = "No user data found"
                    lastWeightTextView.text = "No weight data found"
                    Log.d("Firestore", "No user data found")
                }
            }
            .addOnFailureListener { e ->
                usernameTextView.text = "Error: ${e.message}"
                lastWeightTextView.text = "Error: ${e.message}"
                Log.e("Firestore", "Error fetching user data", e)
            }
    }


    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }
    private fun openVisitsWindow(userId: String) {

        val intent = Intent(this, DoctorVisitsActivity::class.java)
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

    private fun openMedicineWindowActivity(userId: String) {
        val intent = Intent(this, MedicineActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
