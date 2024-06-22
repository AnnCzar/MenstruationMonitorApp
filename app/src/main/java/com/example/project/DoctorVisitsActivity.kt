package com.example.project
//import DoctorVisitsAdapter1
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//import java.util.*
//
//class DoctorVisitsActivity : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: DoctorVisitsAdapter1
//    private lateinit var db: FirebaseFirestore
//    private lateinit var userId: String
//
//    private val doctorsList = mutableListOf<DoctorVisit1>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.visits_window)
//
//
//        db = FirebaseFirestore.getInstance()
//
//
//        recyclerView = findViewById(R.id.visitsRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//
//        userId = intent.getStringExtra("USER_ID") ?: ""
//
//        adapter = DoctorVisitsAdapter1(doctorsList) { selectedDoctorVisit ->
//            showOptionsDialog(selectedDoctorVisit)
//        }
//        recyclerView.adapter = adapter
//
//
//        fetchDoctorVisits()
//
//
//        val addVisitButton: Button = findViewById(R.id.addVisitButton)
//        addVisitButton.setOnClickListener {
//            val intent = Intent(this@DoctorVisitsActivity, AddVisitActivity::class.java)
//            intent.putExtra("USER_ID", userId)
//            startActivity(intent)
//        }
//    }
//
//    private fun fetchDoctorVisits() {
//        db.collection("users").document(userId).collection("doctorVisits")
//            .get()
//            .addOnSuccessListener { result ->
//                doctorsList.clear()
//
//                val currentDate = Date()
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//                for (document in result) {
//                    val doctorName = document.getString("doctorName") ?: ""
//                    val visitDate = document.getString("visitDate") ?: ""
//
//                    if (doctorName.isNotEmpty() && visitDate.isNotEmpty()) {
//                        val visitDateObject = dateFormat.parse(visitDate)
//
//                        // Compare visit date with current date
//                        if (visitDateObject != null && !visitDateObject.before(currentDate)) {
//                            val doctorVisit = DoctorVisit1(
//                                id = document.id,
//                                doctorName = doctorName,
//                                visitDate = visitDate
//                            )
//                            doctorsList.add(doctorVisit)
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                // Error handling
//                e.message?.let {
//                    Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun showOptionsDialog(doctorVisit: DoctorVisit1) {
//        val options = arrayOf("Modify", "Delete")
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Choose an option")
//        builder.setItems(options) { dialog, which ->
//            when (which) {
//                0 -> modifyVisit(doctorVisit)
//                1 -> deleteVisit(doctorVisit)
//            }
//        }
//        builder.show()
//    }
//
//    private fun modifyVisit(doctorVisit: DoctorVisit1) {
//        val intent = Intent(this@DoctorVisitsActivity, ModifyVisitActivity::class.java)
//        intent.putExtra("USER_ID", userId)
//        intent.putExtra("VISIT_ID", doctorVisit.id)
//        startActivity(intent)
//    }
//
//    private fun deleteVisit(doctorVisit: DoctorVisit1) {
//        db.collection("users").document(userId).collection("doctorVisits").document(doctorVisit.id)
//            .delete()
//            .addOnSuccessListener {
//                doctorsList.remove(doctorVisit)
//                adapter.notifyDataSetChanged()
//                Toast.makeText(this, "Visit deleted successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
//


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.AddVisitActivity
import com.example.project.DoctorVisit
import com.example.project.DoctorVisitsAdapter
import com.example.project.ModifyVisitActivity
import com.example.project.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DoctorVisitsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorVisitsAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var homeButtonProfil: ImageButton
    private lateinit var accountWidnowSettingButton: ImageButton


    private val doctorsList = mutableListOf<DoctorVisit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visits_window)

        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.visitsRecyclerView)
        homeButtonProfil = findViewById(R.id.homeButtonProfil)
        accountWidnowSettingButton = findViewById(R.id.accountWidnowSettingButton)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userId = intent.getStringExtra("USER_ID") ?: ""

        adapter = DoctorVisitsAdapter(doctorsList) { selectedDoctorVisit ->
            showOptionsDialog(selectedDoctorVisit)
        }
        recyclerView.adapter = adapter

        fetchDoctorVisits()

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


        val addVisitButton: Button = findViewById(R.id.addVisitButton)
        addVisitButton.setOnClickListener {
            val intent = Intent(this@DoctorVisitsActivity, AddVisitActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }


        accountWidnowSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }
    }

    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctors")
            .get()
            .addOnSuccessListener { result ->
                doctorsList.clear()

                for (document in result) {
                    val doctor = DoctorVisit(
                        id = document.id,
                        doctorName = document.getString("doctorName") ?: "",
                        visitDate = document.getString("visitDate") ?: "",
                        isChecked = document.getBoolean("isChecked") ?: false
                    )
                    doctorsList.add(doctor)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showOptionsDialog(doctorVisit: DoctorVisit) {
        val options = arrayOf("Modify", "Delete")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> modifyVisit(doctorVisit)
                1 -> deleteVisit(doctorVisit)
            }
        }
        builder.show()
    }
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
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

    private fun modifyVisit(doctorVisit: DoctorVisit) {
        val intent = Intent(this@DoctorVisitsActivity, ModifyVisitActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("VISIT_ID", doctorVisit.id)
        startActivity(intent)
    }

    private fun deleteVisit(doctorVisit: DoctorVisit) {
        db.collection("users").document(userId).collection("doctors").document(doctorVisit.id)
            .delete()
            .addOnSuccessListener {
                doctorsList.remove(doctorVisit)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Visit deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
