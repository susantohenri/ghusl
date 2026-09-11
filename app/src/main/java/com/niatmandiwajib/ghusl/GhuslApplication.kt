package com.niatmandiwajib.ghusl

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class GhuslApplication : Application() {

    companion object {
        const val CHANNEL_ID_USTADZ = "tanya_ustadz_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_USTADZ,
                "Tanya Ustadz",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for Tanya Ustadz AI answers"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
