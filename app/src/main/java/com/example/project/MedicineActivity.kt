package com.example.project

import android.content.Intent
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Activity for managing medicines.
 */
class MedicineActivity : AppCompatActivity() {

    private lateinit var medicineRV: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapterModify
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var homeMedications: ImageButton
    private lateinit var settingsMedications: ImageButton
    private lateinit var addMedication: Button
    private lateinit var accountMedications: ImageButton

    private val medicineList = mutableListOf<MedicineList>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medications)

        db = FirebaseFirestore.getInstance()

        medicineRV = findViewById(R.id.medicineRV)
        medicineRV.layoutManager = LinearLayoutManager(this)

        homeMedications = findViewById(R.id.homeMedications)
        settingsMedications = findViewById(R.id.settingsMedications)
        addMedication = findViewById(R.id.addMedication)
//        ODKOMENTOWAĆ
        accountMedications = findViewById(R.id.accountMedications)

        accountMedications.setOnClickListener {
            openAccountWindowActivity(userId)
        }


        userId = intent.getStringExtra("USER_ID") ?: ""

        medicineAdapter = MedicineAdapterModify(medicineList, onEditClick = { medicine ->
            editVisit(medicine)
        }, onDeleteClick = { medicine ->
            deleteVisit(medicine)
        })
        medicineRV.adapter = medicineAdapter

        fetchMedicines()

        addMedication.setOnClickListener {
            val intent = Intent(this@MedicineActivity, AddMedicineActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        settingsMedications.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        homeMedications.setOnClickListener {
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
                    Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    override fun onResume() {
        super.onResume()
        fetchMedicines()

    }

    /**
     * Edits the details of the selected medicine.
     *
     * @param medicine The medicine to be edited.
     */
    private fun editVisit(medicine: MedicineList) {
        val intent = Intent(this, ModifyMedicineActivity::class.java)
        intent.putExtra("MEDICINE_ID", medicine.id)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)

    }

    /**
     * Deletes the selected medicine from the Firestore database.
     *
     * @param medicine The medicine to be deleted.
     */
    private  fun deleteVisit(medicine: MedicineList){
        db.collection("users").document(userId).collection("medicines")
            .document(medicine.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Lek został usunięty", Toast.LENGTH_SHORT).show()
                medicineList.remove(medicine)
                medicineAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Opens the settings activity.
     *
     * @param userId The ID of the current user.
     */
    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    /**
     * Opens the account activity.
     *
     * @param userId The ID of the current user.
     */
    private fun openAccountWindowActivity(userId: String) {
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    /**
     * Fetches the list of medicines from Firestore and updates the UI.
     */
    private fun fetchMedicines() {
        db.collection("users").document(userId).collection("medicines")
            .get()
            .addOnSuccessListener { result ->
                medicineList.clear()
                for (document in result) {
                    val medicine = MedicineList(
                        id = document.id,
                        name = document.getString("medicineName") ?: "",
                        dose = document.getString("doseMedicine") ?: "",
                        time = document.getString("timeMedicine") ?: ""
                    )
                    medicineList.add(medicine)
                }
                medicineAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Saves the check status of a medicine in Firestore.
     *
     * @param medicine The medicine whose status is to be updated.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMedicineCheckStatus(medicine: Medicine) {
        db.collection("users").document(userId)
            .collection("dailyInfo")
            .document(LocalDate.now().toString())
            .collection("medicines")
            .document(medicine.id)
            .set(mapOf("checked" to medicine.isChecked))
            .addOnSuccessListener {
                Toast.makeText(this, "Medicine status updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Opens the main activity for non-pregnant users.
     *
     * @param userId The ID of the current user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPeriodActivity(userId: String) {
        val intent = Intent(this, MainWindowPeriodActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }

    /**
     * Opens the main activity for pregnant users.
     *
     * @param userId The ID of the current user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openMainWindowPregnancyActivity(userId: String) {
        val intent = Intent(this, MainWindowPregnancyActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("SELECTED_DATE", LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        startActivity(intent)
    }
}

private fun <E> MutableList<E>.add(element: Medicine) {

}

