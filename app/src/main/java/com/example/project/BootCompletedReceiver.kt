package com.example.project

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

/**
 * Receiver triggered after the system finishes booting (BOOT_COMPLETED).
 * It sets a daily alarm at 12:00 and launches the main activity of the application.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    private val notificationId = 123

    /**
     * Method called when the system finishes booting or when this receiver is triggered.
     * It sets the alarm and starts the main activity of the app.
     *
     * @param context The context of the application.
     * @param intent The intent that triggered this receiver.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Ustawienie codziennego alarmu o 12:00
            setDailyAlarm(context)

            val mainActivityIntent = Intent(context, MainWindowPeriodActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mainActivityIntent)
        }
    }

    /**
     * Sets a daily alarm at 11:08 AM.
     * The alarm triggers the NotificationReceiver to show a notification.
     *
     * @param context The context of the application.
     */
    private fun setDailyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, NotificationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 8)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }
}

/**
 * Receiver responsible for receiving alarms and showing notifications.
 * It shows a reminder about taking medicine.
 */
class NotificationReceiver : BroadcastReceiver() {

    private val notificationId = 123

    /**
     * Method called when an alarm is received. It shows the medicine reminder notification.
     *
     * @param context The context of the application.
     * @param intent The intent that triggered this receiver.
     */
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context, "Przypomnienie o lekach", "Nie zapomnij o swojej dawce dziś!")
    }

    /**
     * Shows a notification about taking medicine.
     *
     * @param context The context of the application.
     * @param title The title of the notification.
     * @param message The message of the notification.
     */
    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val channelId = "MEDICINE_REMINDER_CHANNEL"

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_medicine_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Powiadomienie zostanie usunięte po kliknięciu

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
