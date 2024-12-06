package com.example.project

data class Chat(
    var sender: String = "",
    
    var message: String = "",
    var receiver: String = "",
    var isSeen: Boolean = false,
    var messageId: String = ""

)
