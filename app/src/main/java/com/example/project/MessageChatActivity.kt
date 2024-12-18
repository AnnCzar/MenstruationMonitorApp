package com.example.project

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.Notifications.Client
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MessageChatActivity : AppCompatActivity() {

    private lateinit var sendImageButton: ImageButton
    private lateinit var messageText: EditText
    private lateinit var displayUserLogin: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val mChatList = ArrayList<Chat>()

    private lateinit var userLogin: String
    private lateinit var userId: String

    private lateinit var firebaseUser: FirebaseUser
    private val db = FirebaseFirestore.getInstance()

    private var notify = false
    private var apiService: APIService? = null

    private var isChatActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        initializeUI()
        initializeFirebase()
        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        setupRecyclerView()
        setupSendButton()

        retrieveMessages(firebaseUser.uid, userId)

    }
    override fun onResume() {
        super.onResume()
        isChatActive = true
    }

    override fun onPause() {
        super.onPause()
        isChatActive = false
    }


    // Inicjalizacja elementów interfejsu użytkownika
    private fun initializeUI() {
        userLogin = intent.getStringExtra("USER_LOGIN") ?: ""
        userId = intent.getStringExtra("USER_ID") ?: ""

        sendImageButton = findViewById(R.id.send_image_button)
        messageText = findViewById(R.id.message_text)
        displayUserLogin = findViewById(R.id.displayLogin)
        chatRecyclerView = findViewById(R.id.messageRV)

        displayUserLogin.text = userLogin
    }

    // Inicjalizacja Firebase i Retrofit
    private fun initializeFirebase() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
    }

    // Konfiguracja RecyclerView
    private fun setupRecyclerView() {
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this, mChatList)
        chatRecyclerView.adapter = chatAdapter
    }

    // Obsługa przycisku wysyłania wiadomości
    private fun setupSendButton() {
        sendImageButton.setOnClickListener {
            val message = messageText.text.toString().trim()
            if (message.isNotEmpty()) {
                notify = true
                sendMessageToUser(firebaseUser.uid, userId, message)
            } else {
                showToast("Error: No text provided")
            }
        }
    }


    // Wysyłanie wiadomości
    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val messageData = mapOf(
            "sender" to senderId,
            "receiver" to receiverId,
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "isseen" to false
        )

        db.collection("Chats").add(messageData)
            .addOnSuccessListener {
                showToast("Wysłano wiadomość do  $userLogin")
                messageText.text.clear()

                // Wyślij powiadomienie push
                if (notify) {

                    notify = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Failed to send message: ${e.message}")
                showToast("Błąd: nie udało się wysłać wiadomości")
            }

        addToChatList(senderId, receiverId)
    }


    // Dodawanie do listy czatów
    private fun addToChatList(senderId: String, receiverId: String) {
        val chatListRef = db.collection("ChatLists")

        chatListRef.document(senderId).collection("Chats").document(receiverId)
            .set(mapOf("id" to receiverId))
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Failed to update chat list for sender: ${e.message}")
            }

        chatListRef.document(receiverId).collection("Chats").document(senderId)
            .set(mapOf("id" to senderId))
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Failed to update chat list for receiver: ${e.message}")
            }
    }

    // Pobieranie wiadomości
    private fun retrieveMessages(senderId: String, receiverId: String) {
        db.collection("Chats")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("MessageChatActivity", "Failed to retrieve messages: ${e.message}")
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { dc ->
                    val chat = dc.document.toObject(Chat::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            if ((chat.receiver == senderId && chat.sender == receiverId) ||
                                (chat.receiver == receiverId && chat.sender == senderId)
                            ) {
                                mChatList.add(chat)
                                chatAdapter.notifyItemInserted(mChatList.size - 1)

                                // Aktualizacja wiadomości jako "seen"
                                if (isChatActive && chat.sender == receiverId && !chat.isseen) {
                                    markMessageAsSeen(dc.document.id)
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val updatedChatIndex = mChatList.indexOfFirst { it.timestamp == chat.timestamp }
                            if (updatedChatIndex != -1) {
                                mChatList[updatedChatIndex] = chat
                                chatAdapter.notifyItemChanged(updatedChatIndex)
                            }
                        }
                        else -> {
                            Log.e("MessageChatActivity", "Nieobsługiwany typ zmiany: ${dc.type}")
                        }
                    }
                }

                // Automatyczne przewijanie do ostatniej wiadomości
                if (mChatList.isNotEmpty()) {
                    chatRecyclerView.scrollToPosition(mChatList.size - 1)
                }
            }
    }

    // Funkcja aktualizująca status wiadomości na "zobaczone"
    private fun markMessageAsSeen(messageId: String) {
        db.collection("Chats").document(messageId).update("isseen", true)
            .addOnSuccessListener {
                Log.d("MessageChatActivity", "Message marked as seen: $messageId")
            }
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Error marking message as seen: ${e.message}")
            }
    }

    // Pomocnicza funkcja wyświetlająca Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



