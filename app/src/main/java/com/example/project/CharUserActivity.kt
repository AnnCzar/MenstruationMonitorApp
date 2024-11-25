package com.example.project

import android.content.Intent
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

class CharUserActivity : AppCompatActivity() {

    private lateinit var chatUserRV: RecyclerView
    private lateinit var chatUserAdapter: ChatUserAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var profileMainWindowDoctor: ImageButton
    private lateinit var settingsMainWindowDoctor: ImageButton


    private val chatUserList = mutableListOf<ChatUser>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_doctor)

        db = FirebaseFirestore.getInstance()

        chatUserRV = findViewById(R.id.chatUserRV)
        chatUserRV.layoutManager = LinearLayoutManager(this)

        profileMainWindowDoctor = findViewById(R.id.AcountButtonMainDoctor)
        settingsMainWindowDoctor = findViewById(R.id.SettingButtonMainDoctor)


        userId = intent.getStringExtra("USER_ID") ?: ""

//        chatUserAdapter = ChatUserAdapter()
        chatUserRV.adapter = chatUserAdapter

        fetchChatUsers()

//        addMedication.setOnClickListener {
//            val intent = Intent(this@MedicineActivity, AddMedicineActivity::class.java)
//            intent.putExtra("USER_ID", userId)
//            startActivity(intent)
//        }

        settingsMainWindowDoctor.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

//        homeMedications.setOnClickListener {
//            val userRef = db.collection("users").document(userId)
//            userRef.get()
//                .addOnSuccessListener { user ->
//                    if (user != null) {
//                        val statusPregnancy = user.getBoolean("statusPregnancy")
//                        if (statusPregnancy != null) {
//                            if (!statusPregnancy) {
//                                openMainWindowPeriodActivity(userId)
//                            } else {
//                                openMainWindowPregnancyActivity(userId)
//                            }
//                        } else {
//                        }
//                    } else {
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
    }
    override fun onResume() {
        super.onResume()
        fetchChatUsers()

    }

    private fun editVisit(medicine: MedicineList) {
        val intent = Intent(this, ModifyMedicineActivity::class.java)
        intent.putExtra("MEDICINE_ID", medicine.id)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)

    }
//    private  fun deleteVisit(medicine: MedicineList){
//        db.collection("users").document(userId).collection("medicines")
//            .document(medicine.id)
//            .delete()
//            .addOnSuccessListener {
//                Toast.makeText(this, "Lek został usunięty", Toast.LENGTH_SHORT).show()
//                medicineList.remove(medicine)
//                medicineAdapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun fetchChatUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                chatUserList.clear()
                for (document in result) {
                    val login = ChatUser(
                        login = document.getString("login") ?: "")
                    chatUserList.add(login)
                }
                chatUserAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

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
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

private fun <E> MutableList<E>.add(element: Medicine) {

}

