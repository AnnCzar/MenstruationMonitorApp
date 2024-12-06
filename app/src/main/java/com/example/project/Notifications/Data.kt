package com.example.project.Notifications

data class Data(
    val sented: String = "",  // Identyfikator użytkownika wysyłającego
    val icon: Int = 0,        // Identyfikator zasobu ikony (Int, np. R.mipmap.ic_launcher)
    val body: String = "",    // Treść wiadomości
    val title: String = "",   // Tytuł powiadomienia
    val user: String = ""     // Identyfikator użytkownika odbierającego
)