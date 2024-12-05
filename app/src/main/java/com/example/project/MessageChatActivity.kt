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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class MessageChatActivity : AppCompatActivity() {

    private lateinit var sendImageButton: ImageButton
    private lateinit var messageText: EditText
    private lateinit var displayUserLogin: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private var mChatList: ArrayList<Chat> = ArrayList()

    private lateinit var userLogin: String
    private lateinit var userId: String

    private lateinit var firebaseUser: FirebaseUser
    private val db = Firebase.firestore // Inicjalizacja Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        // Odbierz dane z Intent
        userLogin = intent.getStringExtra("USER_LOGIN") ?: ""
        userId = intent.getStringExtra("USER_ID") ?: ""
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        // Inicjalizacja widoków
        sendImageButton = findViewById(R.id.send_image_button)
        messageText = findViewById(R.id.message_text)
        displayUserLogin = findViewById(R.id.displayLogin)
        chatRecyclerView = findViewById(R.id.messageRV)

        // Konfiguracja RecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this, mChatList)
        chatRecyclerView.adapter = chatAdapter

        // Wyświetl login użytkownika w TextView
        displayUserLogin.text = userLogin

        // Obsługa kliknięcia przycisku wysyłania
        sendImageButton.setOnClickListener {
            val message = messageText.text.toString()
            if (message.isNotBlank()) {
                sendMessageToUser(firebaseUser.uid, userId, message)
            } else {
                Toast.makeText(this, "Error: No text provided", Toast.LENGTH_SHORT).show()
            }
        }

        // Pobierz wiadomości
        retrieveMessages(firebaseUser.uid, userId)
    }

    /**
     * Funkcja do zapisywania wiadomości w Firestore
     */
    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val messageData = hashMapOf(
            "sender" to senderId,
            "receiver" to receiverId,
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "isseen" to false
        )

        db.collection("Chats")
            .add(messageData)
            .addOnSuccessListener {
                Log.d("MessageChatActivity", "Message successfully saved in Firestore.")
                Toast.makeText(this, "Message sent to $userLogin: $message", Toast.LENGTH_SHORT).show()
                messageText.text.clear() // Wyczyść pole tekstowe po wysłaniu wiadomości
            }
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Failed to save message: ${e.message}")
                Toast.makeText(this, "Error: Could not send message", Toast.LENGTH_SHORT).show()
            }

        // Dodanie użytkowników do listy czatów
        addToChatList(senderId, receiverId)
    }

    /**
     * Funkcja do aktualizacji listy czatów w Firestore
     */
    private fun addToChatList(senderId: String, receiverId: String) {
        val chatListRef = db.collection("ChatLists")
        chatListRef.document(senderId).collection("Chats").document(receiverId)
            .set(hashMapOf("id" to receiverId))
            .addOnSuccessListener {
                Log.d("MessageChatActivity", "Chat list updated for sender.")
            }
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Failed to update chat list for sender: ${e.message}")
            }

        chatListRef.document(receiverId).collection("Chats").document(senderId)
            .set(hashMapOf("id" to senderId))
            .addOnSuccessListener {
                Log.d("MessageChatActivity", "Chat list updated for receiver.")
            }
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Failed to update chat list for receiver: ${e.message}")
            }
    }

    /**
     * Funkcja do pobierania wiadomości z Firestore
     */
    private fun retrieveMessages(senderId: String, receiverId: String) {
        db.collection("Chats")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("MessageChatActivity", "Failed to retrieve messages: ${e.message}")
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val chat = dc.document.toObject(Chat::class.java)
                            if ((chat.receiver == senderId && chat.sender == receiverId) ||
                                (chat.receiver == receiverId && chat.sender == senderId)
                            ) {
                                mChatList.add(chat)
                                chatAdapter.notifyItemInserted(mChatList.size - 1)
                            }
                        }
                        else -> {}
                    }
                }

                // Scroll do ostatniej wiadomości
                if (mChatList.isNotEmpty()) {
                    chatRecyclerView.scrollToPosition(mChatList.size - 1)
                }
            }
    }
}
