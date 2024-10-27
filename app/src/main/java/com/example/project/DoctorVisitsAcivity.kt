package com.example.project

import DoctorVisitAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.RecyclerView
//
// do okna wizyt jak wjedzie sie przez profil

class DoctorVisitsActivity : AppCompatActivity() {

    private lateinit var visitRV: RecyclerView
    private lateinit var visitAdapter: DoctorVisitAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var homeButtonProfil: ImageButton
    private lateinit var accountWidnowSettingButton: ImageButton
    private lateinit var addVisitButton: Button

    private val doctorsList = mutableListOf<DoctorVisit>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visits_window)

        db = FirebaseFirestore.getInstance()

        visitRV = findViewById(R.id.visitRV)
        homeButtonProfil = findViewById(R.id.homeButtonProfil)
        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        addVisitButton = findViewById(R.id.addVisitButton)
        visitRV.layoutManager = LinearLayoutManager(this)

        userId = intent.getStringExtra("USER_ID") ?: ""

        visitAdapter = DoctorVisitAdapter(doctorsList, onEditClick = { visit ->
            editVisit(visit)
        }, onDeleteClick = { visit ->
            deleteVisit(visit)
        })

        visitRV.adapter = visitAdapter

        onResume()

        addVisitButton.setOnClickListener {
            val intent = Intent(this@DoctorVisitsActivity, AddVisitActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        accountWidnowSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

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
                        }
                    } else {
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
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

    override fun onResume() {
        super.onResume()
        doctorsList.clear()
        fetchDoctorVisits()
    }

    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctorVisits")
            .whereEqualTo("checked", false)
            .get()
            .addOnSuccessListener { result ->
                doctorsList.clear()
                for (document in result) {
                    val doctor = DoctorVisit(
                        id = document.id,
                        doctorName = document.getString("doctorName") ?: "",
                        visitDate = document.getString("visitDate") ?: "",
                        time = document.getString("time") ?: "",
                        isChecked = document.getBoolean("checked") ?: false,
                        extraInfo = document.getString("extraInfo") ?: "",
                    )
                    doctorsList.add(doctor)
                }
                visitAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editVisit(visit: DoctorVisit) {
        Toast.makeText(this, "Nie ma edycji", Toast.LENGTH_SHORT).show() 
        val intent = Intent(this, ModifyVisitActivity::class.java)
        intent.putExtra("VISIT_ID", visit.id)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun deleteVisit(visit: DoctorVisit) {
        db.collection("users").document(userId).collection("doctorVisits")
            .document(visit.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Wizyta została usunięta", Toast.LENGTH_SHORT).show()
                doctorsList.remove(visit)
                visitAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
