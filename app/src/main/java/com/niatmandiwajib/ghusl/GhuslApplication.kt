package com.niatmandiwajib.ghusl

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.niatmandiwajib.ghusl.di.AppContainer

class GhuslApplication : Application() {

    lateinit var container: AppContainer
        private set

    companion object {
        const val CHANNEL_ID_USTADZ = "tanya_ustadz_channel"
    }

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
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
