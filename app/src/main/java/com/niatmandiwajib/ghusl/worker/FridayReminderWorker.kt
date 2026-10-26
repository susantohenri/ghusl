package com.niatmandiwajib.ghusl.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.MainActivity
import com.niatmandiwajib.ghusl.R
import java.util.Calendar

class FridayReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Only notify on Thursday (Malam Jumat) or Friday morning
        if (dayOfWeek == Calendar.THURSDAY || dayOfWeek == Calendar.FRIDAY) {
            sendNotification()
        }

        return Result.success()
    }

    private fun sendNotification() {
        val context = applicationContext
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            201,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, GhuslApplication.CHANNEL_ID_REMINDER)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Mandi Sunnah Hari Jum'at")
            .setContentText("Hari Jum'at berkah. Jangan lupa niat dan mandi sunnah sebelum sholat Jum'at.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(201, notification)
    }
}
