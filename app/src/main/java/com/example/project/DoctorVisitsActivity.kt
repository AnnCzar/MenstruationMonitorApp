package com.example.project
import DoctorVisitsAdapter1
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DoctorVisitsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorVisitsAdapter1
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private val doctorsList = mutableListOf<DoctorVisit1>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visits_window)


        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()


        recyclerView = findViewById(R.id.visitsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        userId = intent.getStringExtra("USER_ID") ?: ""

        adapter = DoctorVisitsAdapter1(doctorsList) { selectedDoctorVisit ->
            showOptionsDialog(selectedDoctorVisit)
        }
        recyclerView.adapter = adapter


        fetchDoctorVisits()


        val addVisitButton: Button = findViewById(R.id.addVisitButton)
        addVisitButton.setOnClickListener {
            val intent = Intent(this@DoctorVisitsActivity, AddVisitActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }

    private fun fetchDoctorVisits() {
        db.collection("users").document(userId).collection("doctorVisits")
            .get()
            .addOnSuccessListener { result ->
                doctorsList.clear()

                val currentDate = Date()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                for (document in result) {
                    val doctorName = document.getString("doctorName") ?: ""
                    val visitDate = document.getString("visitDate") ?: ""

                    if (doctorName.isNotEmpty() && visitDate.isNotEmpty()) {
                        val visitDateObject = dateFormat.parse(visitDate)

                        // Compare visit date with current date
                        if (visitDateObject != null && !visitDateObject.before(currentDate)) {
                            val doctorVisit = DoctorVisit1(
                                id = document.id,
                                doctorName = doctorName,
                                visitDate = visitDate
                            )
                            doctorsList.add(doctorVisit)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Error handling
                e.message?.let {
                    Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showOptionsDialog(doctorVisit: DoctorVisit1) {
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

    private fun modifyVisit(doctorVisit: DoctorVisit1) {
        val intent = Intent(this@DoctorVisitsActivity, ModifyVisitActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("VISIT_ID", doctorVisit.id)
        startActivity(intent)
    }

    private fun deleteVisit(doctorVisit: DoctorVisit1) {
        db.collection("users").document(userId).collection("doctorVisits").document(doctorVisit.id)
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

