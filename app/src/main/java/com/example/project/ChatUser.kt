package com.example.project

data class ChatUser(
    val id: String,
    val login: String,
    var timestamp: Long = 0
)
