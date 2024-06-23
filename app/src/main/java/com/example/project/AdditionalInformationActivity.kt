package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class AdditionalInformationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var symptomsAdapter: SymptomsAdapter

    private lateinit var imageButtonHappy: ImageButton
    private lateinit var imageButtonNeutral: ImageButton
    private lateinit var imageButtonSad: ImageButton

    private lateinit var enterWeight: EditText
    private lateinit var addInfoEnterTemperature: EditText

    private lateinit var buttonSaveAddInfo: Button
    private lateinit var addInfoSettingAcountButton: ImageButton
    private lateinit var addInfoSettingButton: ImageButton
    private lateinit var homeButtonaddInfo: ImageButton
    private lateinit var addDate: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private var selectedDate: LocalDate? = null
    private var currentMood: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.additional_information)

        initializeViews()
        configureRecyclerView()
        setListeners()

        userId = intent.getStringExtra("USER_ID") ?: ""
        selectedDate = LocalDate.parse(intent.getStringExtra("SELECTED_DATE"))
        db = FirebaseFirestore.getInstance()

        loadAdditionalInformation(selectedDate)
        addDate.text = selectedDate.toString()
    }

    private fun initializeViews() {
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
    }

    private fun configureRecyclerView() {
        val symptoms = listOf(
            Symptom("Headache", false),
            Symptom("Acne", false),
            Symptom("Stomachache", false),
            Symptom("Breast pain", false),
            Symptom("Dizziness", false),
            Symptom("Fever", false),
            Symptom("Back pain", false),
            Symptom("Swelling", false),
            Symptom("Nervousness", false),
            Symptom("Hunger", false),
            Symptom("Diarrhea", false),
            Symptom("Constipation", false)
        )

        symptomsAdapter = SymptomsAdapter(symptoms) { symptom, isChecked ->
            symptom.isChecked = isChecked
            saveSymptomCheckStatus(symptom)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = symptomsAdapter
    }

    private fun setListeners() {
        buttonSaveAddInfo.setOnClickListener {
            saveAdditionalInformation()
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

        addInfoSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        addInfoSettingAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }
    }

    private fun saveAdditionalInformation() {
        val weight = enterWeight.text.toString()
        val temperature = addInfoEnterTemperature.text.toString()
        val symptomsData = symptomsAdapter.getSymptoms().map { it.name to it.isChecked }.toMap()

        val data = hashMapOf(
            "weight" to weight,
            "temperature" to temperature,
            "symptoms" to symptomsData,
            "mood" to currentMood
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

    private fun loadAdditionalInformation(date: LocalDate?) {
        if (date == null) return

        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(date.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val weight = document.getString("weight") ?: ""
                    val temperature = document.getString("temperature") ?: ""
                    val symptomsMap = document.get("symptoms") as? Map<String, Boolean> ?: emptyMap()

                    enterWeight.setText(weight)
                    addInfoEnterTemperature.setText(temperature)
                    val currentSymptoms = symptomsAdapter.getSymptoms()
                    currentSymptoms.forEach { it.isChecked = symptomsMap[it.name] == true }
                    symptomsAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT). show()
            }
    }

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

    private fun setCurrentMood(mood: String) {
        // Aktualizacja nastroju i zmiana wyglądu przycisku
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
}
