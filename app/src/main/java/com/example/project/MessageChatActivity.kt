package com.example.project

import Token
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
import com.example.project.Notifications.Data
import com.example.project.Notifications.MyResponse
import com.example.project.Notifications.Sender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private val db = Firebase.firestore

    private var notify = false
    private var apiService: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        initializeUI()
        initializeFirebase()
        setupRecyclerView()
        setupSendButton()

        retrieveMessages(firebaseUser.uid, userId)
        setupNotificationListener()
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

    // Słuchacz powiadomień
    private fun setupNotificationListener() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(ChatUser::class.java)
                if (notify) {
                    sendNotification(userId, user?.login, messageText.text.toString())
                    notify = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessageChatActivity", "Notification listener cancelled: ${error.message}")
            }
        })
    }

    // Wysyłanie powiadomienia
    private fun sendNotification(receiverId: String?, userName: String?, message: String) {
        val tokensRef = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = tokensRef.orderByKey().equalTo(receiverId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)
                    val data = Data(firebaseUser.uid,
                        R.mipmap.ic_launcher, "$userName: $message", "New Message", receiverId.toString()
                    )
                    val sender = Sender(data, token?.token.toString())

                    apiService?.sendNotification(sender)?.enqueue(object : Callback<MyResponse> {
                        override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                            if (response.code() == 200 && response.body()?.success != 1) {
                                showToast("Failed to send notification")
                            }
                        }

                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            Log.e("MessageChatActivity", "Notification failed: ${t.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessageChatActivity", "Notification query cancelled: ${error.message}")
            }
        })
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
                showToast("Message sent to $userLogin")
                messageText.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e("MessageChatActivity", "Failed to send message: ${e.message}")
                showToast("Error: Could not send message")
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
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val chat = dc.document.toObject(Chat::class.java)
                        if ((chat.receiver == senderId && chat.sender == receiverId) ||
                            (chat.receiver == receiverId && chat.sender == senderId)
                        ) {
                            mChatList.add(chat)
                            chatAdapter.notifyItemInserted(mChatList.size - 1)
                        }
                    }
                }

                // Automatyczne przewijanie do ostatniej wiadomości
                if (mChatList.isNotEmpty()) {
                    chatRecyclerView.scrollToPosition(mChatList.size - 1)
                }
            }
    }

    // Pomocnicza funkcja wyświetlająca Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}