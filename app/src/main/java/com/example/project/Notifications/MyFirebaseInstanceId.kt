package com.example.project.Notifications

import Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        // Sprawdzenie, czy użytkownik jest zalogowany, i aktualizacja tokenu
        if (firebaseUser != null) {
            updateToken(token)
        }
    }

    private fun updateToken(token: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference("Tokens")

        // Tworzenie instancji tokenu
        val tokenObject = Token(token)

        // Aktualizacja tokenu w bazie danych dla bieżącego użytkownika
        ref.child(firebaseUser!!.uid).setValue(tokenObject)
            .addOnSuccessListener {
                // Logika po sukcesie, jeśli jest potrzebna
            }
            .addOnFailureListener { e ->
                // Obsługa błędów
                e.printStackTrace()
            }
    }
}