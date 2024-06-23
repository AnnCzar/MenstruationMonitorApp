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

class BootCompletedReceiver : BroadcastReceiver() {

    private val notificationId = 123 // Unikalny identyfikator powiadomienia

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Ustawienie codziennego alarmu o 12:00
            setDailyAlarm(context)

            // Uruchomienie głównej aktywności
            val mainActivityIntent = Intent(context, MainWindowPeriodActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mainActivityIntent)
        }
    }

    private fun setDailyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, NotificationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 45)
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

class NotificationReceiver : BroadcastReceiver() {

    private val notificationId = 123 // Unikalny identyfikator powiadomienia

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context, "Przypomnienie o lekach", "Nie zapomnij o swojej dawce dziś!")
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
