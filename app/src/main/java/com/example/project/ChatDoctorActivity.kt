package com.example.project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChatDoctorActivity : AppCompatActivity() {

    private lateinit var chatUserRV: RecyclerView
    private lateinit var chatUserAdapter: ChatDoctorAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var logoutButton: ImageButton
    private lateinit var auth: FirebaseAuth


    private val chatUserList = ArrayList<ChatUser>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_doctor)

        db = FirebaseFirestore.getInstance()

        chatUserRV = findViewById(R.id.chatUserRV)
        chatUserRV.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()

        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logout()

        }





        userId = intent.getStringExtra("USER_ID") ?: ""

        chatUserAdapter = ChatDoctorAdapter(usersNames = chatUserList) { chatUser ->
            openMessageChatActivity(chatUser)
        }
        chatUserRV.adapter = chatUserAdapter

        updateUserTimestamp(userId)
        fetchChatUsers()





        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(
                    this@ChatDoctorActivity,
                    "Cofanie jest wyłączone!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })



        FirebaseMessaging.getInstance().subscribeToTopic("user_${userId}")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("FCM", msg)
            }
        db.collection("Chats")
            .whereEqualTo("receiver", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore dupa 1", "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }
                Log.d(
                    "Firestore dupa ",
                    "Snapshot received for doctor: ${snapshots?.documents?.size ?: 0} documents"
                )
                // Znajdź najnowszą wiadomość (na podstawie timestamp)
                val latestMessage = snapshots?.documents
                    ?.map { it.toObject(Message::class.java) }
                    ?.maxByOrNull { it?.timestamp ?: 0L }
                if (latestMessage != null) {
                    Log.d("Firestore dupa 3", "Latest message for doctor: ${latestMessage.message}")
                    sendNotification(latestMessage)
                }
            }
        FirebaseMessaging.getInstance().subscribeToTopic("user_${userId}")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) "Subscribed" else "Subscription failed"
                Log.d("FCM", msg)
            }
        db.collection("Chats") // Poprawiona nazwa kolekcji
            .whereEqualTo("receiver", userId) // Użycie poprawnej nazwy pola "receiver"
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }

                // Znalezienie najnowszej wiadomości na podstawie pola "timestamp"
                val latestMessage = snapshots?.documents
                    ?.mapNotNull { it.toObject(Message::class.java) } // Konwersja do obiektu Message
                    ?.maxByOrNull { it.timestamp ?: 0L }

                if (latestMessage != null) {
                    sendNotification(latestMessage)
                }
            }


    }

    // Klasa Message odpowiadająca strukturze dokumentu w Firestore
    data class Message(
        val message: String = "",
        val receiver: String = "",
        val sender: String = "",
        val timestamp: Long = 0L,
        val isseen: Boolean = false
    )


//
//    private fun sendNotification(message: Message) {
////        Log.d("Notifications dupa 4", "Preparing notification for message: ${message.message}")
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "default",
//                "Default Notifications",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//        val notification = NotificationCompat.Builder(this, "default")
//            .setContentTitle("Nowa wiadomość od ${message.sender}")
//            .setContentText(message.message)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true)
//            .build()
//        Log.d("Notifications dupa 5", "Sending notification with timestamp: ${message.timestamp}")
//        notificationManager.notify(message.timestamp.toInt(), notification)
//    }

    private fun sendNotification(message: ChatDoctorActivity.Message) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(message.sender)
            .get()
            .addOnSuccessListener { document ->
                val senderLogin = document.getString("login") ?: "Nieznany użytkownik"

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "default",
                        "Default Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(this, "default")
                    .setContentTitle("Nowa wiadomość od $senderLogin")
                    .setContentText(message.message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(message.timestamp.toInt(), notification)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    override fun onResume() {
        super.onResume()
        fetchChatUsers()

    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            putString("USER_ID", null)
            apply()
        }
        auth.signOut()


        val intent = Intent(this, LoginWindowActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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

    private fun updateUserTimestamp(userId: String) {
        db.collection("users").document(userId)
            .update("timestamp", System.currentTimeMillis())
            .addOnSuccessListener {
                Log.d("Firestore", "Timestamp zaktualizowany dla użytkownika: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Błąd podczas aktualizacji timestamp: ${e.message}", e)
            }
    }


    private fun fetchChatUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { userDocuments ->
                val allUsers = mutableListOf<ChatUser>()

                for (document in userDocuments) {
                    val login = document.getString("login") ?: "Nieznany użytkownik"
                    val id = document.id
                    allUsers.add(
                        ChatUser(
                            login = login,
                            id = id,
                            timestamp = 0L
                        )
                    )
                }

                db.collection("Chats")
                    .whereEqualTo("receiver", userId)
                    .get()
                    .addOnSuccessListener { chatDocuments ->
                        val latestMessages =
                            mutableMapOf<String, Long>()
                        for (document in chatDocuments) {
                            val sender = document.getString("sender") ?: continue
                            val timestamp = document.getLong("timestamp") ?: 0L
                            if (latestMessages[sender] == null || timestamp > latestMessages[sender]!!) {
                                latestMessages[sender] = timestamp
                            }
                        }

                        for (user in allUsers) {
                            if (latestMessages.containsKey(user.id)) {
                                user.timestamp = latestMessages[user.id]!!
                            }
                        }

                        chatUserList.clear()
                        chatUserList.addAll(allUsers.sortedWith(compareByDescending<ChatUser> { it.timestamp > 0L }.thenByDescending { it.timestamp }))

                        chatUserAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun <E> MutableList<E>.add(element: Medicine) {

    }
}
