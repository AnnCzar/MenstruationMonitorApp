package com.example.project

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class AdditionalInformationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var symptoms: MutableList<Symptom>
    private lateinit var symptomsAdapter: SymptomsAdapter
    private lateinit var imageButtonHappyColor: ImageButton
    private lateinit var imageButtonNeutralColor: ImageButton
    private lateinit var imageButtonSadColor: ImageButton

    private lateinit var imageButtonHappy: ImageButton
    private lateinit var imageButtonNeutral: ImageButton
    private lateinit var imageButtonSad: ImageButton
    private var currentMood: String = ""

    private lateinit var drinksCountText: TextView
    private lateinit var increaseDrinkButton: Button
    private lateinit var decreaseDrinkButton: Button
    private var drinksCount: Int = 0

    private lateinit var enterWeight: EditText
    private lateinit var addInfoEnterTemperature: EditText

    private lateinit var buttonSaveAddInfo: Button
    private lateinit var addInfoSettingAcountButton: ImageButton
    private lateinit var addInfoSettingButton: ImageButton
    private lateinit var homeButtonaddInfo: ImageButton
    private lateinit var addDate: TextView
    private lateinit var textWarning: TextView
    private lateinit var spinner: Spinner

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private var selectedDate: LocalDate? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.additional_information)
        db = FirebaseFirestore.getInstance()

        symptoms = mutableListOf(
            Symptom("Ból głowy", false),
            Symptom("Trądzik", false),
            Symptom("Ból brzucha", false),
            Symptom("Ból piersi", false),
            Symptom("Zawroty głowy", false),
            Symptom("Gorączka", false),
            Symptom("Ból pleców", false),
            Symptom("Obrzęki", false),
            Symptom("Podenerwowanie", false),
            Symptom("Głód", false),
            Symptom("Biegunka", false),
            Symptom("Zaparcie", false)
        )

        userId = intent.getStringExtra("USER_ID") ?: ""
        selectedDate = LocalDate.parse(intent.getStringExtra("SELECTED_DATE"))
        initializeViews()
        configureRecyclerView()
        setListeners()

        addDate.text = selectedDate.toString()

        restoreMoodState()
        fetchSymptomsForWeek()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViews() {
        spinner = findViewById(R.id.spinner)
        homeButtonaddInfo = findViewById(R.id.homeButtonaddInfo)
        imageButtonHappyColor = findViewById(R.id.imageButtonHappyColor)
        imageButtonNeutralColor = findViewById(R.id.imageButtonNeutralColor)
        imageButtonSadColor = findViewById(R.id.imageButtonSadColor)
        imageButtonHappy = findViewById(R.id.imageButtonHappy)
        imageButtonNeutral = findViewById(R.id.imageButtonNeutral)
        imageButtonSad = findViewById(R.id.imageButtonSad)
        textWarning = findViewById(R.id.textWarning)
        enterWeight = findViewById(R.id.enterWeight)
        addInfoEnterTemperature = findViewById(R.id.addInfoEnterTemperature)
        recyclerView = findViewById(R.id.recyclerViewSymptoms)

        buttonSaveAddInfo = findViewById(R.id.buttonSaveAddInfo)
        addInfoSettingAcountButton = findViewById(R.id.addInfoSettingAcountButton)
        addInfoSettingButton = findViewById(R.id.addInfoSettingButton)
        addDate = findViewById(R.id.addDate)
        drinksCountText = findViewById(R.id.drinksCountText)
        increaseDrinkButton = findViewById(R.id.increaseDrinkButton)
        decreaseDrinkButton = findViewById(R.id.decreaseDrinkButton)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.mucus_types,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureRecyclerView() {

        symptomsAdapter = SymptomsAdapter(symptoms) { symptom, isChecked ->
            symptom.isChecked = isChecked
            saveSymptomCheckStatus(symptom)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = symptomsAdapter

        loadAdditionalInformation(selectedDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        buttonSaveAddInfo.setOnClickListener {
            saveAdditionalInformation()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedOption = parent.getItemAtPosition(position).toString()
                saveMucusTypeToDatabase(selectedOption)

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        imageButtonHappy.setOnClickListener {
            if (!imageButtonHappy.isSelected) {
                setCurrentMood("happy")
                saveMoodToDatabase("happy")
                saveMoodToPreferences("happy")
                imageButtonHappy.isSelected = true
                imageButtonHappy.visibility = Button.GONE
                imageButtonHappyColor.visibility = Button.VISIBLE
                resetOtherButtons("happy")
            }
        }

        imageButtonNeutral.setOnClickListener {
            if (!imageButtonNeutral.isSelected) {
                setCurrentMood("neutral")
                saveMoodToDatabase("neutral")
                saveMoodToPreferences("neutral")
                imageButtonNeutral.isSelected = true
                imageButtonNeutral.visibility = Button.GONE
                imageButtonNeutralColor.visibility = Button.VISIBLE
                resetOtherButtons("neutral")
            }
        }

        imageButtonSad.setOnClickListener {
            if (!imageButtonSad.isSelected) {
                setCurrentMood("sad")
                saveMoodToDatabase("sad")
                saveMoodToPreferences("sad")
                imageButtonSad.isSelected = true
                imageButtonSad.visibility = Button.GONE
                imageButtonSadColor.visibility = Button.VISIBLE
                resetOtherButtons("sad")
            }
        }

        homeButtonaddInfo.setOnClickListener {
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

        addInfoSettingAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }
        addInfoSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        increaseDrinkButton.setOnClickListener {
            drinksCount++
            updateDrinkCount()
            updateDrinkCountInFirestore()
        }

        decreaseDrinkButton.setOnClickListener {
            if (drinksCount > 0) {
                drinksCount--
                updateDrinkCount()
                updateDrinkCountInFirestore()
            } else {
                Toast.makeText(this, "Wprowadzona ilość płynów nie może być ujemna.", Toast.LENGTH_SHORT).show()
            }
        }

        fetchTodaysDrinkCount()
    }


    // ZAPIS DANYCH

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAdditionalInformation() {
        val weight = enterWeight.text.toString()

        val temperatureInput = addInfoEnterTemperature.text.toString()

        val temperature = if (temperatureInput.isBlank()) null else temperatureInput.toDoubleOrNull()
        if (temperatureInput.isNotBlank() && temperature == null) {
            addInfoEnterTemperature.error = "Wprowadź poprawną temperaturę"
            return
        }

        val symptomsData = symptomsAdapter.getSymptoms().map { it.name to it.isChecked }.toMap()

        val selectedMucusType = spinner.selectedItem.toString()
        if (selectedMucusType.isBlank()) {
            Toast.makeText(this, "Proszę wybrać typ śluzu", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "weight" to weight,
            "temperature" to temperatureInput,
            "symptoms" to symptomsData,
            "mood" to currentMood,
            "mucusType" to selectedMucusType
        )

        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(selectedDate?.toString() ?: LocalDate.now().toString())
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Dane zapisane", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        db.collection("users").document(userId)
            .update("weight", weight.toDouble())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMucusTypeToDatabase(mucusType: String) {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(selectedDate?.toString() ?: LocalDate.now().toString())
            .update("mucusType", mucusType)
            .addOnSuccessListener {
                Toast.makeText(this, "Zaktualizowano typ śluzu", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd przy aktualizowaniu typu śluzu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveSymptomCheckStatus(symptom: Symptom) {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(selectedDate?.toString() ?: LocalDate.now().toString())
            .collection("additionalInfo")
            .document(symptom.name)
            .set(mapOf("checked" to symptom.isChecked))
            .addOnSuccessListener {
                Toast.makeText(this, "Stan objawów został zaktualizowany", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun saveMoodToPreferences(mood: String) {
        val sharedPreferences = getSharedPreferences("MoodPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("currentMood", mood)
        editor.putString("date", getCurrentDate())
        editor.apply()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMoodToDatabase(mood: String) {
        val weight = enterWeight.text.toString()
        val temperature = addInfoEnterTemperature.text.toString()
        val symptomsData = symptomsAdapter.getSymptoms().map { it.name to it.isChecked }.toMap()

        val userDocRef = db.collection("users").document(userId)
        val dailyInfoDocRef = userDocRef.collection("dailyInfo").document(selectedDate?.toString() ?: LocalDate.now().toString())
        dailyInfoDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    dailyInfoDocRef.update("mood", mood)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Humor zaktualizowany", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Błąd przy aktualizowaniu nastroju: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    val data = hashMapOf(
                        "weight" to weight,
                        "temperature" to temperature,
                        "symptoms" to symptomsData,
                        "mood" to mood
                    )

                    dailyInfoDocRef.set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Zapisano nowy nastrój", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Błąd przy zapisywaniu nastroju: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd przy sprawdzaniu czy dokument z dodatkowymi informacjami z dnia istnieje: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // UPDATE
    private fun updateDrinkCountInFirestore() {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(selectedDate.toString())
            .update("drinksCount", drinksCount)
            .addOnSuccessListener {
                Toast.makeText(this, "Liczba najpojów zaktualizowana w bazie danych", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd przy aktualizacji liczby napojów: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDrinkCount() {
        drinksCountText.text = drinksCount.toString()
    }


    // fetch
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchSymptomsForWeek() {
        val userDailyInfoRef = db.collection("users").document(userId).collection("dailyInfo")
        val today = LocalDate.now()
        val datesToCheck = (0..6).map { today.minusDays(it.toLong()).toString() }

        val tasks = datesToCheck.map { date ->
            userDailyInfoRef.document(date).get()
        }

        Tasks.whenAllComplete(tasks).addOnSuccessListener { completedTasks ->
            val symptomFrequency = mutableMapOf<String, Int>()

            completedTasks.forEach { task ->
                val documentSnapshot = (task.result as? DocumentSnapshot)
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val symptoms = documentSnapshot.get("symptoms") as? Map<String, Boolean>
                    symptoms?.forEach { (symptomName, isChecked) ->
                        if (isChecked) {
                            symptomFrequency[symptomName] = symptomFrequency.getOrDefault(symptomName, 0) + 1
                        }
                    }
                }
            }

            Log.d("FetchSymptoms", "Symptom Frequency Map: $symptomFrequency")

            val repeatedSymptoms = symptomFrequency.filter { it.value == 7 }

            runOnUiThread {
                if (repeatedSymptoms.isNotEmpty()) {
                    val symptomList = repeatedSymptoms.keys.joinToString(", ")
                    textWarning.visibility = View.VISIBLE
                    textWarning.text =
                        "Następujące symptomy występują każdego dnia przez tydzień: $symptomList.\n Skontaktuj się z lekarzem!"
                    Log.d("FetchSymptoms", "Updated TextView with symptoms: $symptomList")
                } else {
                    textWarning.visibility = View.GONE
                    Log.d("FetchSymptoms", "No symptoms repeated for 7 days.")
                }
            }
        }.addOnFailureListener { e ->
            runOnUiThread {
                Toast.makeText(this, "Błąd w odczycie danych: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FetchSymptoms", "Error fetching data: ${e.message}")
            }
        }
    }
    private fun loadAdditionalInformation(date: LocalDate?) {
        if (date == null) return

        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(date.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {

                    val symptomsMap = try {
                        document.get("symptoms") as? Map<String, Boolean> ?: emptyMap()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyMap<String, Boolean>()
                    }

                    symptoms.forEach { symptom ->
                        symptom.isChecked = symptomsMap[symptom.name] == true
                    }

                    symptomsAdapter.notifyDataSetChanged() // odswiezenie adaptera

                    val weight = document.getString("weight") ?: ""

                    enterWeight.setText(weight)

                    val temperature: Double? = try {
                        document.getDouble("temperature")
                            ?: document.getString("temperature")?.toDoubleOrNull()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

                    addInfoEnterTemperature.setText(temperature?.toString() ?: "")

                    val mucusType = document.getString("mucusType") ?: "Brak danych"
                    val adapter = spinner.adapter as ArrayAdapter<String>
                    val position = adapter.getPosition(mucusType)
                    spinner.setSelection(if (position >= 0) position else 0)

                } else {
                    enterWeight.setText("")
                    addInfoEnterTemperature.setText("")
                    spinner.setSelection(0)
                    symptomsAdapter.getSymptoms().forEach { it.isChecked = false }
                    symptomsAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd pobierania danych: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
    }


    private fun fetchTodaysDrinkCount() {
        db.collection("users").document(userId).collection("dailyInfo")
            .document(selectedDate.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    drinksCount = document.getLong("drinksCount")?.toInt() ?: 0
                    updateDrinkCount()
                } else {
                    val defaultDailyInfo = hashMapOf(
                        "drinksCount" to 0
                    )
                    db.collection("users").document(userId).collection("dailyInfo")
                        .document(selectedDate.toString())
                        .set(defaultDailyInfo)
                        .addOnSuccessListener {
                            drinksCount = 0
                            updateDrinkCount()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Błąd przy tworzeniu informacji z dnia: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd podczas pobierania liczby napojów: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


// ----------- POZOSTAŁE FUNKCJE
    fun loadMoodFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("MoodPrefs", Context.MODE_PRIVATE)
        val savedDate = sharedPreferences.getString("date", "")
        return if (savedDate == getCurrentDate()) {
            sharedPreferences.getString("currentMood", null)
        } else {
            null
        }
    }

    fun restoreMoodState() {
        val savedMood = loadMoodFromPreferences()

        when (savedMood) {
            "happy" -> {
                imageButtonHappy.isSelected = true
                imageButtonHappy.visibility = Button.GONE
                imageButtonHappyColor.visibility = Button.VISIBLE
                resetOtherButtons("happy")
            }
            "neutral" -> {
                imageButtonNeutral.isSelected = true
                imageButtonNeutral.visibility = Button.GONE
                imageButtonNeutralColor.visibility = Button.VISIBLE
                resetOtherButtons("neutral")
            }
            "sad" -> {
                imageButtonSad.isSelected = true
                imageButtonSad.visibility = Button.GONE
                imageButtonSadColor.visibility = Button.VISIBLE
                resetOtherButtons("sad")
            }
        }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun resetOtherButtons(currentMood: String) {
        when (currentMood) {
            "happy" -> {
                imageButtonNeutral.isSelected = false
                imageButtonNeutral.visibility = Button.VISIBLE
                imageButtonNeutralColor.visibility = Button.GONE

                imageButtonSad.isSelected = false
                imageButtonSad.visibility = Button.VISIBLE
                imageButtonSadColor.visibility = Button.GONE
            }
            "neutral" -> {
                imageButtonHappy.isSelected = false
                imageButtonHappy.visibility = Button.VISIBLE
                imageButtonHappyColor.visibility = Button.GONE

                imageButtonSad.isSelected = false
                imageButtonSad.visibility = Button.VISIBLE
                imageButtonSadColor.visibility = Button.GONE
            }
            "sad" -> {
                imageButtonHappy.isSelected = false
                imageButtonHappy.visibility = Button.VISIBLE
                imageButtonHappyColor.visibility = Button.GONE

                imageButtonNeutral.isSelected = false
                imageButtonNeutral.visibility = Button.VISIBLE
                imageButtonNeutralColor.visibility = Button.GONE
            }
        }
    }

        private fun setCurrentMood(mood: String) {
        currentMood = mood
        when (mood) {
            "happy" -> {
                imageButtonHappy.isSelected = true
                imageButtonNeutral.isSelected = false
                imageButtonSad.isSelected = false
            }
            "neutral" -> {
                imageButtonHappy.isSelected = false
                imageButtonNeutral.isSelected = true
                imageButtonSad.isSelected = false
            }
            "sad" -> {
                imageButtonHappy.isSelected = false
                imageButtonNeutral.isSelected = false
                imageButtonSad.isSelected = true
            }
        }
    }

// NAWIGACJA
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun openAccountWindowActivity(userId: String) {
        val intent = Intent (this, AccountWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
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
}
