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
import kotlinx.coroutines.withContext

class GhuslApplication : Application() {

    lateinit var container: AppContainer
        private set

    lateinit var adManager: AdManager
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        const val CHANNEL_ID_USTADZ = "tanya_ustadz_channel"
        const val CHANNEL_ID_REMINDER = "sunnah_reminder_channel"
    }

    override fun onCreate() {
        super.onCreate()
        com.niatmandiwajib.ghusl.analytics.AnalyticsLogger.init(this)
        container = AppContainer(this)
        createNotificationChannels()
        initializeAds()
        scheduleContentPreload()
        scheduleFridayReminder()
    }

    private fun scheduleContentPreload() {
        try {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()
            val request = androidx.work.OneTimeWorkRequestBuilder<com.niatmandiwajib.ghusl.worker.ContentPreloadWorker>()
                .setConstraints(constraints)
                .build()
            androidx.work.WorkManager.getInstance(this).enqueueUniqueWork(
                "ContentPreloadWork",
                androidx.work.ExistingWorkPolicy.KEEP,
                request
            )
        } catch (e: Exception) {
            // WorkManager enqueue failed
        }
    }

    private fun scheduleFridayReminder() {
        try {
            val request = androidx.work.PeriodicWorkRequestBuilder<com.niatmandiwajib.ghusl.worker.FridayReminderWorker>(
                24, java.util.concurrent.TimeUnit.HOURS
            ).build()
            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "FridayReminderWork",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        } catch (e: Exception) {
            // Periodic work enqueue failed
        }
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
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDER,
                "Pengingat Ibadah",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pengingat Mandi Sunnah Hari Jum'at"
            }
            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(reminderChannel)
        }
    }

    private fun initializeAds() {
        adManager = AdManager(this)
        applicationScope.launch {
            try {
                val adConfig = withContext(Dispatchers.IO) {
                    container.adRepository.getAdConfig()
                }
                adManager.initialize(adConfig)
                // Start app open ad observer on Main thread
                val observer = AppOpenAdObserver(this@GhuslApplication, adManager)
                observer.start()
            } catch (e: Exception) {
                // Ads init failed silently — app works without ads
            }
        }
    }
}
