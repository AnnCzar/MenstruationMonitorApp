package com.example.project


/**
 * Data class representing a chat message.
 * This class stores details of a chat message including the sender, receiver, message content,
 * whether the message has been seen, a unique message ID, and the timestamp when the message was sent.
 *
 * @property sender The user who sent the message.
 * @property message The content of the message.
 * @property receiver The user who is receiving the message.
 * @property isseen Boolean flag indicating whether the message has been seen by the receiver.
 * @property messageId A unique identifier for the message.
 * @property timestamp The timestamp when the message was sent, represented as a long value.
 */
data class Chat(
    var sender: String = "",
    var message: String = "",
    var receiver: String = "",
    var isseen: Boolean = false,
    var messageId: String = "",
    val timestamp: Long = 0L,

    )