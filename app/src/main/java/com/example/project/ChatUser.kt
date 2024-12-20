package com.example.project

/**
 * Represents a chat user with necessary details.
 *
 * @property id Unique identifier for the user, typically the user's UID.
 * @property login The username or login name of the user.
 * @property timestamp The last activity timestamp of the user, defaulting to 0.
 * This could be used to track when the user was last active or for sorting.
 */
data class ChatUser(
    val id: String,
    val login: String,
    var timestamp: Long = 0
)
