
package com.example.project


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.components.YAxis
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.github.mikephil.charting.formatter.ValueFormatter
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

//        chartWeightTemperature = findViewById(R.id.chartWeightTemperature)
    }

    private fun loadUserInfo() {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("login") ?: "N/A"
                    usernameTextView.text = username
                } else {
                    usernameTextView.text = "No user data found"
                }
            }
            .addOnFailureListener { e ->
                usernameTextView.text = "Error: ${e.message}"
            }

        db.collection("users").document(userId)
            .collection("dailyInfo")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val lastWeight = document.getString("weight") ?: "N/A"
                    lastWeightTextView.text = lastWeight
                }
            }
            .addOnFailureListener { e ->
                lastWeightTextView.text = "Error: ${e.message}"
            }
    }

//    private fun loadChartData() {
//        val dates = mutableListOf<LocalDate>()
////        val weights = mutableListOf<Entry>()
////        val temperatures = mutableListOf<Entry>()
//
//        db.collection("users").document(userId)
//            .collection("dailyInfo")
//            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
//            .limit(7)
//            .get()
//            .addOnSuccessListener { documents ->
//                var index = 0
//                for (document in documents) {
//                    val date = document.getString("date")?.let { LocalDate.parse(it) }
//                    val weight = document.getString("weight")?.toFloatOrNull() ?: 0f
//                    val temperature = document.getString("temperature")?.toFloatOrNull() ?: 0f
//
//                    date?.let {
//                        dates.add(it)
////                        weights.add(Entry(index.toFloat(), weight))
////                        temperatures.add(Entry(index.toFloat(), temperature))
//                    }
//                    index++
//                }
//
//                val weightDataSet = LineDataSet(weights, "Weight").apply {
//                    color = Color.BLUE
//                    axisDependency = YAxis.AxisDependency.LEFT
//                }
//
//                val temperatureDataSet = LineDataSet(temperatures, "Temperature").apply {
//                    color = Color.RED
//                    axisDependency = YAxis.AxisDependency.RIGHT
//                }
//
//                val lineData = LineData(weightDataSet, temperatureDataSet)
//                chartWeightTemperature.data = lineData
//                chartWeightTemperature.invalidate()
//
//                val xAxis = chartWeightTemperature.xAxis
//                xAxis.position = XAxis.XAxisPosition.BOTTOM
//                xAxis.setDrawGridLines(false)
//                xAxis.valueFormatter = object : ValueFormatter() {
//                    override fun getFormattedValue(value: Float): String {
//                        return dates.getOrNull(value.toInt())?.toString() ?: value.toString()
//                    }
//                }
//
//                val leftAxis = chartWeightTemperature.axisLeft
//                leftAxis.setDrawGridLines(false)
//
//                val rightAxis = chartWeightTemperature.axisRight
//                rightAxis.setDrawGridLines(false)
//            }
//            .addOnFailureListener { e ->
//                // Handle error here
//            }
//    }

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
}
