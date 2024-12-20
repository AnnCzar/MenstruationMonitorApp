package com.example.project

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the chat logic, handling the message list,
 * and communicating with the generative AI model to send and receive messages.
 */
class ChatViewModel : ViewModel() {

    /**
     * A list to hold the chat messages. The list is mutable and updates when
     * new messages are sent or received.
     */
    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    /**
     * Instance of the generative AI model used for sending and receiving messages.
     * It is initialized with a specific model name and API key.
     */
    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.apiKey
    )

    /**
     * Sends a message to the generative AI model and processes the response.
     *
     * @param question The user's message/question to be sent to the AI.
     * This function handles the communication with the generative AI model
     * and updates the message list accordingly.
     */
    fun sendMessage(question : String){
        viewModelScope.launch {

            try{
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role){ text(it.message) }
                    }.toList()
                )

                messageList.add(MessageModel(question,"user"))
                messageList.add(MessageModel("...","model"))

                val response = chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(),"model"))
            }catch (e : Exception){
                messageList.removeLast()
                messageList.add(MessageModel("Error : "+e.message.toString(),"model"))
            }
        }
    }
}