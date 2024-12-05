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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

class SettingsWindowActivity : AppCompatActivity(){
    private lateinit var homeButtonSetting: ImageButton
    private lateinit var settingWindowAcountButton: ImageButton
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var temperatureChart: GraphView
    private lateinit var weightChart: GraphView
    private lateinit var waterChart: GraphView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)


        homeButtonSetting = findViewById(R.id.homeButtonSetting)
        settingWindowAcountButton = findViewById(R.id.settingWindowAcountButton)
        temperatureChart = findViewById(R.id.temperatureChart)
        weightChart = findViewById(R.id.weightChart)
        waterChart = findViewById(R.id.waterChart)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            loadChartData()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadChartData() {
        val dailyInfoRef = db.collection("users").document(userId).collection("dailyInfo")

        dailyInfoRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val temperaturePoints = mutableListOf<DataPoint>()
                    val weightPoints = mutableListOf<DataPoint>()
                    val waterPoints = mutableListOf<DataPoint>()

                    for (document in snapshot.documents) {
                        val date = document.id
                        val temperature = document.getString("temperature")?.toDoubleOrNull()
                        val weight = document.getString("weight")?.toDoubleOrNull()
                        val drinksCount = document.getLong("drinksCount")?.toDouble()

                        val dateIndex = LocalDate.parse(date, DateTimeFormatter.ISO_DATE).toEpochDay().toDouble()

                        temperature?.let { temperaturePoints.add(DataPoint(dateIndex, it)) }
                        weight?.let { weightPoints.add(DataPoint(dateIndex, it)) }
                        drinksCount?.let { waterPoints.add(DataPoint(dateIndex, it)) }
                    }

                    // Dodanie danych do wykresów
                    temperatureChart.addSeries(LineGraphSeries(temperaturePoints.toTypedArray()))
                    weightChart.addSeries(LineGraphSeries(weightPoints.toTypedArray()))
                    waterChart.addSeries(LineGraphSeries(waterPoints.toTypedArray()))

                    // Konfiguracja wykresów
                    configureChart(temperatureChart, "Temperatura")
                    configureChart(weightChart, "Waga")
                    configureChart(waterChart, "Ilość wody")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading chart data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configureChart(chart: GraphView, title: String) {
        chart.title = title
        chart.viewport.isScalable = true
        chart.viewport.isScrollable = true
        chart.viewport.setScalableY(true)
        chart.viewport.setScrollableY(true)
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