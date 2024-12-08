package com.example.project

import com.example.project.MainWindowPregnancyActivity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle

import android.widget.Button

import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


class SettingsWindowActivity : AppCompatActivity() {
    private lateinit var homeButtonSetting: ImageButton
    private lateinit var settingWindowAcountButton: ImageButton

    private lateinit var buttonChangePassword: Button

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
        buttonChangePassword = findViewById(R.id.buttonChangePassword)


        temperatureChart = findViewById(R.id.temperatureChart)
        weightChart = findViewById(R.id.weightChart)
        waterChart = findViewById(R.id.waterChart)

        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()

        loadChartData()
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
                    Toast.makeText(
                        this@SettingsWindowActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


        buttonChangePassword.setOnClickListener {
            openChangePassword(userId)


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadChartData() {
        val dailyInfoRef = db.collection("users").document(userId).collection("dailyInfo")
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM") // Format: dzień-miesiąc
        dailyInfoRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val temperaturePoints = mutableListOf<DataPoint>()
                    val weightPoints = mutableListOf<DataPoint>()
                    val waterPoints = mutableListOf<DataPoint>()
                    val dateLabels = mutableListOf<String>() // Etykiety dla osi X
                    val selectedDateLabels = mutableListOf<String>() // Wybrane etykiety

                    snapshot.documents.forEachIndexed { index, document ->
                        val date = document.id // Zakładamy, że format daty to "YYYY-MM-DD"
                        val temperatureRaw = document.get("temperature")
                        val weight = document.getString("weight")?.toDoubleOrNull()
                        val drinksCount = document.getLong("drinksCount")?.toDouble()

                        val temperature = when (temperatureRaw) {
                            is Number -> temperatureRaw.toDouble()
                            is String -> temperatureRaw.toDoubleOrNull()
                            else -> null
                        }

                        // Konwersja daty na lokalną i formatowanie do "dd-MM"
                        val formattedDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                            .format(dateFormatter)

                        // Dodanie etykiety co 5. dzień lub dla pierwszego/ostatniego
                        if (index % 10 == 0 || index == 0 || index == snapshot.size() - 1) {
                            selectedDateLabels.add(formattedDate)
                        } else {
                            selectedDateLabels.add("") // Puste miejsce na osi
                        }

                        // Użycie indeksu jako wartości na osi X
                        val xValue = index.toDouble()

                        temperature?.let { temperaturePoints.add(DataPoint(xValue, it)) }
                        weight?.let { weightPoints.add(DataPoint(xValue, it)) }
                        drinksCount?.let { waterPoints.add(DataPoint(xValue, it)) }
                    }

                    // Dodanie danych do wykresów
                    temperatureChart.addSeries(LineGraphSeries(temperaturePoints.toTypedArray()))
                    weightChart.addSeries(LineGraphSeries(weightPoints.toTypedArray()))
                    waterChart.addSeries(LineGraphSeries(waterPoints.toTypedArray()))

                    temperatureChart.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
                    temperatureChart.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);

// Ustawianie kolorów tytułów osi na wykresie temperatureChart
                    temperatureChart.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
                    temperatureChart.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);

// Ustawianie podpisów osi X i Y na wykresie weightChart
                    weightChart.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
                    weightChart.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);

// Ustawianie kolorów tytułów osi na wykresie weightChart
                    weightChart.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
                    weightChart.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);

// Ustawianie podpisów osi X i Y na wykresie waterChart
                    waterChart.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
                    waterChart.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);

// Ustawianie kolorów tytułów osi na wykresie waterChart
                    waterChart.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
                    waterChart.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
                    // Konfiguracja wykresów
                    configureChart(temperatureChart, "Temperatura", selectedDateLabels)
                    configureChart(weightChart, "Waga", selectedDateLabels)
                    configureChart(waterChart, "Ilość wody", selectedDateLabels)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error loading chart data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }



    private fun configureChart(chart: GraphView, title: String, dateLabels: List<String>) {
        chart.title = title
        chart.viewport.isScalable = true
        chart.viewport.isScrollable = true
        chart.viewport.setScalableY(true)
        chart.viewport.setScrollableY(true)

        val staticLabelsFormatter = StaticLabelsFormatter(chart)
        staticLabelsFormatter.setHorizontalLabels(dateLabels.toTypedArray()) // Ustawienie dat jako etykiet
        chart.gridLabelRenderer.labelFormatter = staticLabelsFormatter

        // Ustawienia dla osi
        chart.gridLabelRenderer.horizontalAxisTitle = "Data"
        chart.gridLabelRenderer.verticalAxisTitle = when (title) {
            "Waga" -> "kg"
            "Ilość wody" -> "szklanki"
            else -> "°C"
        }

        chart.gridLabelRenderer.isHorizontalLabelsVisible = true
        chart.gridLabelRenderer.isVerticalLabelsVisible = true
        chart.titleColor = Color.BLACK
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

        private fun openAccountWindowActivity(userId: String) {
            val intent = Intent(this, AccountWindowActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }


        private fun openChangePassword(userId: String) {
            val intent = Intent(this, ChangePassword::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

    }
