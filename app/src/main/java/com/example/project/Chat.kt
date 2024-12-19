package com.example.project

data class Chat(
    var sender: String = "",
    var message: String = "",
    var receiver: String = "",
    var isseen: Boolean = false,
    var messageId: String = "",
    val timestamp: Long = 0L,

    )