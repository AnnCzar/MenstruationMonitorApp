package com.example.project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChatUserActivity : AppCompatActivity() {

    private lateinit var chatUserRV: RecyclerView
    private lateinit var chatUserAdapter: ChatDoctorAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var chatUserAcountButton: ImageButton
    private lateinit var chatUserSettingButton: ImageButton
    private lateinit var chatUserHomeButton: ImageButton
    private lateinit var  doctorType: Spinner

    private val chatUserList = ArrayList<ChatUser>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_list_user)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("USER_ID") ?: ""

        chatUserRV = findViewById(R.id.chatUserRV)
        chatUserRV.layoutManager = LinearLayoutManager(this)
        doctorType = findViewById(R.id.doctorType)

        val doctorAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.doctor_type,
            R.layout.spinner_item
        )
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        doctorType.adapter = doctorAdapter

        doctorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSpecialization = parent?.getItemAtPosition(position).toString()

                when (selectedSpecialization) {
                    "Wszyscy" -> fetchAllChatUsers()
                    else -> fetchChatUsers(selectedSpecialization)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        chatUserAcountButton = findViewById(R.id.chatUserAcountButton)
        chatUserSettingButton = findViewById(R.id.chatUserSettingButton)
        chatUserHomeButton = findViewById(R.id.chatUserHomeButton)


        chatUserAdapter = ChatDoctorAdapter(usersNames = chatUserList) { chatUser ->
            openMessageChatActivity(chatUser)
        }
        chatUserRV.adapter = chatUserAdapter


        chatUserSettingButton.setOnClickListener {
            openSettingsWindowActivity(userId)
        }

        chatUserAcountButton.setOnClickListener {
            openAccountWindowActivity(userId)
        }
        chatUserHomeButton.setOnClickListener {
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
                    Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


// FETCHE
    private fun fetchChatUsers(specialisation: String) {
        db.collection("users")
            .whereEqualTo("role", "Lekarz")
            .whereEqualTo("specialisation", specialisation)
            .get()
            .addOnSuccessListener { userDocuments ->
                val allUsers = mutableListOf<ChatUser>()

                for (document in userDocuments) {
                    val login = document.getString("login") ?: "Nieznany użytkownik"
                    val id = document.id
                    allUsers.add(ChatUser(login = login, id = id, timestamp = 0L))
                }

                fetchAndSortUsers(allUsers)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAllChatUsers() {
        db.collection("users")
            .whereEqualTo("role", "Lekarz")
            .get()
            .addOnSuccessListener { userDocuments ->
                val allUsers = mutableListOf<ChatUser>()

                for (document in userDocuments) {
                    val login = document.getString("login") ?: "Nieznany użytkownik"
                    val id = document.id
                    allUsers.add(ChatUser(login = login, id = id, timestamp = 0L))
                }

                fetchAndSortUsers(allUsers)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAndSortUsers(allUsers: MutableList<ChatUser>) {
        db.collection("Chats")
            .whereEqualTo("receiver", userId)
            .get()
            .addOnSuccessListener { chatDocuments ->
                val latestMessages = mutableMapOf<String, Long>()

                for (document in chatDocuments) {
                    val sender = document.getString("sender") ?: continue
                    val timestamp = document.getLong("timestamp") ?: 0L
                    if (latestMessages[sender] == null || timestamp > latestMessages[sender]!!) {
                        latestMessages[sender] = timestamp
                    }
                }

                for (user in allUsers) {
                    user.timestamp = latestMessages[user.id] ?: 0L
                }

                chatUserList.clear()
                chatUserList.addAll(
                    allUsers.sortedWith(
                        compareByDescending<ChatUser> { it.timestamp > 0L }
                            .thenByDescending { it.timestamp }
                    )
                )

                chatUserAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // NAWIGACJA

    private fun openAccountWindowActivity(userId: String) {
        val intent = Intent(this, AccountWindowActivity::class.java)
        intent.putExtra("USER_ID", userId)
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
    private fun openMessageChatActivity(chatUser: ChatUser) {
        val intent = Intent(this, MessageChatActivity::class.java).apply {
            putExtra("USER_LOGIN", chatUser.login)
            putExtra("USER_ID", chatUser.id)
        }
        startActivity(intent)
    }


    private fun openSettingsWindowActivity(userId: String) {
        val intent = Intent(this, SettingsWindowActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }
}


private fun <E> MutableList<E>.add(element: Medicine) {

}

