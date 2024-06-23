package com.example.project

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class BootCompletedReceiver : BroadcastReceiver() {

    private val notificationId = 123 // Unikalny identyfikator powiadomienia

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Wywołanie powiadomienia
            showNotification(context, "Przypomnienie o lekach", "Nie zapomnij o swojej dawce dziś!")

            // Uruchomienie głównej aktywności
            val mainActivityIntent = Intent(context, MainWindowPeriodActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mainActivityIntent)
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        // ID kanału powiadomień, do którego ma być przypisane powiadomienie
        val channelId = "MEDICINE_REMINDER_CHANNEL"

        // Tworzenie powiadomienia
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_medicine_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Powiadomienie zostanie usunięte po kliknięciu

        // Wysyłanie powiadomienia
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
