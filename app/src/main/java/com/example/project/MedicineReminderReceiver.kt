package com.example.project

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * BroadcastReceiver for handling medicine reminders.
 *
 * This receiver is triggered at a specific time to show a notification reminding the user to take their medicine.
 */
class MedicineReminderReceiver : BroadcastReceiver() {

    /**
     * Handles the receipt of the broadcast.
     *
     * @param context The application context.
     * @param intent The intent that was broadcast.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val notificationIntent = Intent(context, MainWindowPeriodActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "MEDICINE_REMINDER_CHANNEL")
            .setSmallIcon(R.drawable.ic_medicine_reminder)
            .setContentTitle("Przypomnienie o lekach")
            .setContentText("Czas na wzięcie leków.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notification)
    }
}
