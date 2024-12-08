package com.example.project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdditionalInformationActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var symptoms: MutableList<Symptom>
    private lateinit var symptomsAdapter: SymptomsAdapter

    private lateinit var imageButtonHappy: ImageButton
    private lateinit var imageButtonNeutral: ImageButton
    private lateinit var imageButtonSad: ImageButton

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

    private lateinit var spinner: Spinner

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private var selectedDate: LocalDate? = null
    private var currentMood: String = ""

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

        loadAdditionalInformation(selectedDate)
        addDate.text = selectedDate.toString()

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
                        } else {
                        }
                    } else {
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
    }

    private fun initializeViews() {
        spinner = findViewById(R.id.spinner)
        homeButtonaddInfo = findViewById(R.id.homeButtonaddInfo)

        imageButtonHappy = findViewById(R.id.imageButtonHappy)
        imageButtonNeutral = findViewById(R.id.imageButtonNeutral)
        imageButtonSad = findViewById(R.id.imageButtonSad)

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
            setCurrentMood("happy")
            saveMoodToDatabase("happy")
            imageButtonHappy.isSelected = true
            // Pozostałe przyciski są odznaczone
            imageButtonNeutral.isSelected = false
            imageButtonSad.isSelected = false
        }

        imageButtonNeutral.setOnClickListener {
            setCurrentMood("neutral")
            saveMoodToDatabase("neutral")
            imageButtonNeutral.isSelected = true
            // Pozostałe przyciski są odznaczone
            imageButtonHappy.isSelected = false
            imageButtonSad.isSelected = false
        }

        imageButtonSad.setOnClickListener {
            setCurrentMood("sad")
            saveMoodToDatabase("sad")
            imageButtonSad.isSelected = true
            // Pozostałe przyciski są odznaczone
            imageButtonHappy.isSelected = false
            imageButtonNeutral.isSelected = false
        }


//        addInfoSettingButton.setOnClickListener {
//            openSettingsWindowActivity(userId)
//        }
//
//        addInfoSettingAcountButton.setOnClickListener {
//            openAccountWindowActivity(userId)
//        }
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
                Toast.makeText(this, "Drinks count cannot be negative.", Toast.LENGTH_SHORT).show()
            }
        }

        fetchTodaysDrinkCount()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMucusTypeToDatabase(mucusType: String) {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(selectedDate?.toString() ?: LocalDate.now().toString())
            .update("mucusType", mucusType)
            .addOnSuccessListener {
                Toast.makeText(this, "Mucus type updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating mucus type: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this, "Error creating daily info: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching drinks count: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateDrinkCountInFirestore() {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(selectedDate.toString())
            .update("drinksCount", drinksCount)
            .addOnSuccessListener {
                Toast.makeText(this, "Drinks count updated in Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating drinks count: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDrinkCount() {
        drinksCountText.text = drinksCount.toString()
    }

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
                Toast.makeText(this, "Information saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Symptom status updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    // Teraz zaktualizuj stany objawów w adapterze
                    symptoms.forEach { symptom ->
                        // Ustaw stan zaznaczenia dla każdego objawu
                        symptom.isChecked = symptomsMap[symptom.name] == true
                    }

                    symptomsAdapter.notifyDataSetChanged() // Odświeżenie adaptera

                    val weight = document.getString("weight") ?: ""

                    enterWeight.setText(weight)

                    // Pobranie temperatury z bezpiecznym rzutowaniem
                    val temperature: Double? = try {
                        document.getDouble("temperature")
                            ?: document.getString("temperature")?.toDoubleOrNull()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    // Ustawienie temperatury w polu tekstowym
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
                            Toast.makeText(this, "Mood updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error updating mood: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this, "New mood saved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error saving mood: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error checking dailyInfo document existence: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

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
