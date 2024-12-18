package com.example.project.Notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.project.MessageChatActivity
import com.example.project.Notifications.OreoNotification.Companion.CHANNEL_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging : FirebaseMessagingService() {
    override fun onMessageReceived(mRemoteMessage: RemoteMessage) {
        super.onMessageReceived(mRemoteMessage)
        val sented = mRemoteMessage.data["sented"]
        val user = mRemoteMessage.data["user"]

//        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
//        val currentOnlineUser = sharedPref.getString("currentUser", "none")

//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//
//        if (firebaseUser != null && sented == firebaseUser.uid) {
//            if (currentOnlineUser != user) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    sendOreoNotification(mRemoteMessage)
//                } else {
//                    sendNotification(mRemoteMessage)
//                }
//            }
//        }
//    }
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && sented == firebaseUser.uid) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendOreoNotification(mRemoteMessage)
            } else {
                sendNotification(mRemoteMessage)
            }
        }
    }

    private fun sendNotification(mRemoteMessage: RemoteMessage) {
        val user = mRemoteMessage.data["user"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]
        val icon = mRemoteMessage.data["icon"]

        // Zamiana `user` na liczbę do wykorzystania w `PendingIntent`
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()

        // Utworzenie intencji do otwierania MessageChatActivity
        val intent = Intent(this, MessageChatActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putString("userid", user) // Ustawienie odbiorcy w pakiecie
            putExtras(bundle)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, j, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE // IMMUTABLE dla nowszych wersji Androida
        )

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID) // Użycie kanału powiadomień
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Wyświetlenie powiadomienia
        noti.notify(j, builder.build())
    }

    private fun sendOreoNotification(mRemoteMessage: RemoteMessage) {
        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        // Zamiana `user` na liczbę do wykorzystania w `PendingIntent`
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()

        val intent = Intent(this, MessageChatActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putString("userid", user) // Ustawienie odbiorcy w pakiecie
            putExtras(bundle)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, j, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Utworzenie OreoNotification
        val oreoNotification = OreoNotification(this)
        val builder = oreoNotification.getOreoNotification(
            title, body, pendingIntent, defaultSound, icon.toString()
        )

        oreoNotification.getManager!!.notify(j, builder.build())
    }
}