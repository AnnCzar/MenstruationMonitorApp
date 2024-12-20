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

/**
 * Activity for the doctor's chat interface.
 * Displays a list of users who have sent messages to the doctor, and allows interaction with them.
 * It also includes handling for notifications when new messages are received.
 */
class ChatDoctorActivity : AppCompatActivity() {

    private lateinit var chatUserRV: RecyclerView
    private lateinit var chatUserAdapter: ChatDoctorAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var logoutButton: ImageButton
    private lateinit var auth: FirebaseAuth


    private val chatUserList = ArrayList<ChatUser>()

    /**
     * Called when the activity is first created.
     * Initializes UI components, sets up the RecyclerView, and subscribes to Firebase messaging for notifications.
     * @param savedInstanceState Bundle containing saved state of the activity (if any).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window_doctor)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = intent.getStringExtra("USER_ID") ?: ""

        chatUserRV = findViewById(R.id.chatUserRV)
        chatUserRV.layoutManager = LinearLayoutManager(this)
        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logout()

        }
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

        // WYSYŁANIE POWIADOMIEN O NIEODCZYTANEJ WIADOMOŚCI
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
            .whereEqualTo("isseen", false)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                // znajdywnaie wiadomosci (na podstawie timestamp)
                val latestMessage = snapshots?.documents
                    ?.map { it.toObject(Message::class.java) }
                    ?.maxByOrNull { it?.timestamp ?: 0L }
                if (latestMessage != null) {
                    sendNotification(latestMessage)
                }
            }
        FirebaseMessaging.getInstance().subscribeToTopic("user_${userId}")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) "Subscribed" else "Subscription failed"
                Log.d("FCM", msg)
            }
        db.collection("Chats")
            .whereEqualTo("receiver", userId)
            .whereEqualTo("isseen", false)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed: ${e.message}", e)
                    return@addSnapshotListener
                }

                val latestMessage = snapshots?.documents
                    ?.mapNotNull { it.toObject(Message::class.java) }
                    ?.maxByOrNull { it.timestamp ?: 0L }

                if (latestMessage != null) {
                    sendNotification(latestMessage)
                }
            }
    }

    /**
     * Called when the activity is resumed, fetching the latest chat users.
     */
    override fun onResume() {
        super.onResume()
        fetchChatUsers()

    }

    /**
     * Sends a notification to the user when a new message is received.
     * Retrieves the sender's login and displays the message content.
     *
     * @param message The message object containing details about the message.
     */
    private fun sendNotification(message: Message) {
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


    /**
     * Logs the user out, clearing shared preferences and navigating back to the login screen.
     */
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

    /**
     * Updates the user's timestamp in Firestore.
     *
     * @param userId The ID of the user whose timestamp should be updated.
     */
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

    /**
     * Fetches the list of users from Firestore and updates the chat user list, showing the latest message from each user.
     */
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

    /**
     * Navigates to the chat screen for a specific user.
     *
     * @param chatUser The user whose chat should be opened.
     */
    private fun openMessageChatActivity(chatUser: ChatUser) {
        val intent = Intent(this, MessageChatActivity::class.java).apply {
            putExtra("USER_LOGIN", chatUser.login)
            putExtra("USER_ID", chatUser.id)
        }
        startActivity(intent)
    }

    /**
     * Data class representing a message sent in the chat.
     *
     * @param message The content of the message.
     * @param receiver The ID of the user receiving the message.
     * @param sender The ID of the user sending the message.
     * @param timestamp The timestamp of the message.
     * @param isseen Whether the message has been seen by the receiver.
     */
    data class Message(
        val message: String = "",
        val receiver: String = "",
        val sender: String = "",
        val timestamp: Long = 0L,
        val isseen: Boolean = false
    )
}
