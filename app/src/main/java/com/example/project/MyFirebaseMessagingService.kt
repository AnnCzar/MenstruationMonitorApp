package com.example.project

import android.content.Intent
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage

import android.content.Context
import android.app.NotificationManager

/**
 * Custom Firebase Messaging Service for handling push notifications.
 *
 * This service is responsible for receiving push notifications from Firebase Cloud Messaging (FCM) and displaying them to the user.
 * It also handles refreshing the token for the device when needed.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when a new message is received from Firebase Cloud Messaging.
     *
     * This method is triggered when a message is sent from the Firebase console or via the FCM SDK.
     * If the message contains data payload (key-value pairs), it extracts the message and sender ID,
     * and calls the `showNotification` method to display the notification to the user.
     *
     * @param remoteMessage The remote message received from FCM.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            val message = remoteMessage.data["message"]
            val senderId = remoteMessage.data["senderId"]
            showNotification(message ?: "No message", senderId ?: "Unknown Sender")
        }
    }

    /**
     * Displays a notification for the received message.
     *
     * This method creates a notification with the provided message and sender ID.
     * It uses `NotificationCompat.Builder` to build the notification and `NotificationManager` to display it.
     *
     * @param message The content of the notification message.
     * @param senderId The ID of the sender.
     */
    private fun showNotification(message: String, senderId: String) {
        val notificationId = 1
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "default")
            .setContentTitle("New message from $senderId")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Called when the device receives a new FCM registration token.
     *
     * This method logs the new token to the console. The token is required to send notifications to the device.
     *
     * @param token The new registration token for the device.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }
}

