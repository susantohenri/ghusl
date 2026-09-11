package com.niatmandiwajib.ghusl

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.niatmandiwajib.ghusl.ads.AdManager
import com.niatmandiwajib.ghusl.ads.AppOpenAdObserver
import com.niatmandiwajib.ghusl.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GhuslApplication : Application() {

    lateinit var container: AppContainer
        private set

    lateinit var adManager: AdManager
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val CHANNEL_ID_USTADZ = "tanya_ustadz_channel"
    }

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        createNotificationChannels()
        initializeAds()
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

    private fun initializeAds() {
        adManager = AdManager(this)
        applicationScope.launch {
            try {
                val adConfig = container.adRepository.getAdConfig()
                adManager.initialize(adConfig)
                // Start app open ad observer
                val observer = AppOpenAdObserver(this@GhuslApplication, adManager)
                observer.start()
            } catch (e: Exception) {
                // Ads init failed silently — app works without ads
            }
        }
    }
}
