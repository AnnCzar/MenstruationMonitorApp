package com.example.project

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * BroadcastReceiver for sending period reminders as notifications.
 *
 * This class triggers a notification reminding the user of an upcoming period. The notification contains a
 * direct link to the main window of the period tracking application.
 */
class periodReminder : BroadcastReceiver() {

    /**
     * Called when the BroadcastReceiver is triggered. Creates and shows a notification reminding the user
     * of an upcoming period.
     *
     * @param context The context from which the receiver was triggered.
     * @param intent The intent that was used to trigger this receiver.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val notificationIntent = Intent(context, MainWindowPeriodActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, "PERIOD_REMINDER_CHANNEL")
            .setSmallIcon(R.drawable.ic_medicine_reminder)
            .setContentTitle("Przypomnienie o zbliżającym się okresie")
            .setContentText("Wkrótce rozpocznie się twój okres.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notification)
    }
}
