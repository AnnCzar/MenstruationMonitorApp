package com.example.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DayPregnancyActivity : AppCompatActivity(){
    private lateinit var cycleDayPregnancy: TextView
//    private lateinit var medicineCheckboxPregnancyDay: EditText
    private lateinit var doctorsListPregnancy: ListView
    private lateinit var additionalInfoPregnancy: Button
    private lateinit var dayPregnancySettingButton: ImageButton
    private lateinit var dayPregnancyAcountButton: ImageButton
    private lateinit var homeButtonCalendar: ImageButton
    private lateinit var medicineRecyclerView: RecyclerView
    private lateinit var doctorRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private lateinit var doctorAdapter: DoctorVisitsAdapter



    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.day_period)
//
        cycleDayPregnancy = findViewById(R.id.cycleDayPregnancy)
//        medicineCheckboxPregnancyDay = findViewById(R.id.medicineCheckboxPregnancyDay)
        doctorsListPregnancy = findViewById(R.id.doctorsListPregnancy)
        additionalInfoPregnancy = findViewById(R.id.additionalInfoPregnancy)
        dayPregnancySettingButton = findViewById(R.id.dayPregnancySettingButton)
        dayPregnancyAcountButton = findViewById(R.id.dayPregnancyAcountButton)
        homeButtonCalendar = findViewById(R.id.homeButtonCalendar)
        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)
        doctorRecyclerView = findViewById(R.id.doctorsRecyclerView)




        userId = intent.getStringExtra("USER_ID") ?: ""
        db = FirebaseFirestore.getInstance()







        // nasłuchiwanie na kliknięcie przycisku - obsługa kliknięica przycisku
        additionalInfoPregnancy.setOnClickListener {
            openAdditionalInformationActivity(userId)
        }
        dayPregnancySettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        dayPregnancyAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }





    }
    private fun openAdditionalInformationActivity(userId: String) {
        val intent = Intent(this, AdditionalInformationActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openAccountWindowActivity(userId: String){
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openHomeWindowActivity(userId: String){
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

}
