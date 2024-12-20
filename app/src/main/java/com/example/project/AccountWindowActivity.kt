package com.example.project

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * AccountWindowActivity provides the main account interface for the user.
 * It displays user-specific data such as username, weight, and cycle information.
 * It also allows navigation to other activities in the application.
 */
class AccountWindowActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var accountWidnowSettingButton: ImageButton
    private lateinit var homeButtonProfil: ImageButton
    private lateinit var usernameTextView: TextView
    private lateinit var lastWeightTextView: TextView
    private lateinit var visitsButton: Button
    private lateinit var medicationsButton: Button
    private lateinit var chatButton: Button
    private lateinit var begginingPregnancyButton: Button
    private lateinit var logoutButton: ImageButton
    private lateinit var mapSearch: Button
    private lateinit var meanCycle: TextView
    private lateinit var meanMenstruation: TextView
    private lateinit var chatBot: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_window)

        initializeViews()

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        loadUserInfo()
        fetchCycleData()

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
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /**
     * Called when the activity resumes.
     * Checks the user's pregnancy status and updates the UI accordingly.
     */
    override fun onResume() {
        super.onResume()
        checkPregnantStatus()
    }

    /**
     * Initializes the UI elements and sets their onClick listeners.
     */
    private fun initializeViews() {
        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        homeButtonProfil = findViewById(R.id.homeButtonProfil)
        usernameTextView = findViewById(R.id.usernameTextView)
        lastWeightTextView = findViewById(R.id.lastWeightTextView)
        visitsButton = findViewById(R.id.visitsButton)
        medicationsButton = findViewById(R.id.medicationsButton)
        mapSearch = findViewById(R.id.mapSearch)
        chatBot = findViewById(R.id.chatBot)
        chatButton =findViewById(R.id.contactDoctor)
        meanCycle = findViewById(R.id.meanCycle)
        meanMenstruation = findViewById(R.id.meanMenstruation)
        begginingPregnancyButton = findViewById(R.id.endingPregnancyButton)
        logoutButton = findViewById(R.id.logoutButton)

        accountWidnowSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        chatButton.setOnClickListener {
            openChatWindowActivity(userId)
        }

        visitsButton.setOnClickListener {
            openVisitsWindow(userId)
        }

        logoutButton.setOnClickListener {
            logout()
        }
        chatBot.setOnClickListener {
            openChatBot(userId)
        }

        begginingPregnancyButton.setOnClickListener {

            openPregnancyBegginingActivity(userId)
        }
        medicationsButton.setOnClickListener {
            openMedicineWindowActivity(userId)
        }
        mapSearch.setOnClickListener {
            openMapWindowActivity(userId)
        }
    }

    /**
     * Logs out the user by clearing session data and returning to the login screen.
     */
    private fun logout() {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            putString("USER_ID", null)
            apply()
        }
        auth.signOut()
        val intent = Intent(this, FirstWindowActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Checks the pregnancy status of the user and updates the UI visibility of certain buttons.
     */
    private fun checkPregnantStatus() {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    val statusPregnancy = user.getBoolean("statusPregnancy")
                    if (statusPregnancy != null) {
                        if (statusPregnancy) {
                            begginingPregnancyButton.visibility = Button.GONE

                        } else {
                            begginingPregnancyButton.visibility = Button.VISIBLE
                        }
                    }
                }
            }
    }

    /**
     * Fetches and displays cycle and menstruation data for the user.
     */
    private fun fetchCycleData() {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    val cycleLength = user.getDouble("cycleLength")?.toInt()
                    meanCycle.text = "$cycleLength dni"
                    val menstruationLength = user.getDouble("periodLength")?.toInt()
                    meanMenstruation.text = "$menstruationLength dni"
                }
            }
    }

    /**
     * Loads user information, such as username and weight, from the Firestore database.
     */
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
                                val latestDateDocument = documents.documents[0]
                                latestDateDocument.reference.collection("dailyInfo")
                                    .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener { dateDocuments ->
                                        if (!dateDocuments.isEmpty) {
                                            val lastWeight = dateDocuments.documents[0].getDouble("weight") ?: 0.0
                                            lastWeightTextView.text = "$lastWeight kg"
                                            Log.d("Firestore", "Last weight from dailyInfo: $lastWeight")
                                        } else {
                                            lastWeightTextView.text = "$userWeight kg"

                                            Log.d("Firestore", "No dailyInfo data found, using user weight: $userWeight")
                                        }
                                    }
                                    .addOnFailureListener { e ->

                                        lastWeightTextView.text = "$userWeight kg"
                                        Log.e("Firestore", "Error fetching dailyInfo date data, using user weight: $userWeight", e)
                                    }
                            } else {
                                lastWeightTextView.text = "$userWeight kg"

                                Log.d("Firestore", "No dailyInfo data found, using user weight: $userWeight")
                            }
                        }
                        .addOnFailureListener { e ->
                            lastWeightTextView.text = "$userWeight kg"

                            Log.e("Firestore", "Error fetching dailyInfo data, using user weight: $userWeight", e)
                        }
                } else {
                    usernameTextView.text = "No user data found"
                    lastWeightTextView.text = "No weight data found"
                    Log.d("Firestore", "No user data found")
                }
            }
            .addOnFailureListener { e ->
                usernameTextView.text = "Błąd: ${e.message}"
                lastWeightTextView.text = "Błąd: ${e.message}"
                Log.e("Firestore", "Error fetching user data", e)
            }
    }

    // Navigation methods for other activities
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun openChatWindowActivity(userId: String) {
        val intent = Intent(this, ChatUserActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun openVisitsWindow(userId: String) {
        val intent = Intent(this, DoctorVisitsActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }

    private fun openMedicineWindowActivity(userId: String) {
        val intent = Intent(this, MedicineActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun openMapWindowActivity(userId: String) {
        val intent = Intent(this, MapActivityPlaces::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun openChatBot(userId: String) {
        val intent = Intent(this, GeminiChatBot::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun openPregnancyBegginingActivity(userId: String) {
        val intent = Intent(this, PregnancyBegginingActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
