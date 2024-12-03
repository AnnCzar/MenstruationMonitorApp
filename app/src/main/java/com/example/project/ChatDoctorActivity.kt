package com.example.project

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChatDoctorActivity : AppCompatActivity() {

    private lateinit var chatUserRV: RecyclerView
    private lateinit var chatUserAdapter: ChatDoctorAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var profileMainWindowDoctor: ImageButton
    private lateinit var settingsMainWindowDoctor: ImageButton


    private val chatUserList = ArrayList<ChatUser>()

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

        chatUserAdapter = ChatDoctorAdapter(usersNames = chatUserList) { chatUser ->
            openMessageChatActivity(chatUser)
        }
        chatUserRV.adapter = chatUserAdapter


        fetchChatUsers()


        settingsMainWindowDoctor.setOnClickListener {
            openSettingsWindowActivity(userId)
        }


    }
    override fun onResume() {
        super.onResume()
        fetchChatUsers()

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

    private fun fetchChatUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                chatUserList.clear()
                for (document in result) {
                    val login = document.getString("login")
                    val id = document.id // ID dokumentu z Firestore
                    if (login.isNullOrEmpty() && id.isEmpty()) {
                        Toast.makeText(this, "Login field is missing", Toast.LENGTH_SHORT).show()
                    } else {
                        chatUserList.add(ChatUser(login = login.toString(), id = id))
                    }
                }
                chatUserAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}

private fun <E> MutableList<E>.add(element: Medicine) {

}

