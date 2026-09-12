package com.niatmandiwajib.ghusl.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.MainActivity
import com.niatmandiwajib.ghusl.R

/**
 * FCM service for receiving push notifications.
 * Fase 1: receives local-triggered notifications and FCM messages.
 * TODO fase 2: pindahkan panggilan Gemini ke GitHub Actions proxy, lihat catatan arsitektur.
 *   GitHub Actions akan mengirim FCM push notification setelah memproses jawaban.
 */
class GhuslFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "GhuslFCM"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // TODO fase 2: kirim token ke backend (GitHub Actions / Firestore)
        // untuk bisa mengirim push notification spesifik ke device ini
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "FCM message received: ${message.data}")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.ustadz_notification_title)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: getString(R.string.ustadz_submitted)

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, GhuslApplication.CHANNEL_ID_USTADZ)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
