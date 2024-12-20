package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent // Ważny import
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider

/**
 * Main activity for the Gemini ChatBot application.
 * This activity uses Jetpack Compose to define its UI and interacts with a [ChatViewModel]
 * to manage the chat data and state.
 */
class GeminiChatBot : ComponentActivity() {

    /**
     * Called when the activity is first created. Initializes the chat view model
     * and sets up the Compose-based UI using a [Scaffold].
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains the previous data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        setContent {
            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ChatPage(modifier = Modifier.padding(innerPadding), chatViewModel) // Wywołanie ChatPage
                }
            }
        }
    }
}
