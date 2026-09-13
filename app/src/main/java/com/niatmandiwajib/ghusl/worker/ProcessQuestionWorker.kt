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
import com.niatmandiwajib.ghusl.data.local.database.GhuslDatabase
import com.niatmandiwajib.ghusl.data.remote.api.GeminiService
import com.niatmandiwajib.ghusl.data.repository.QnARepository
import com.niatmandiwajib.ghusl.domain.model.QnAStatus
import kotlinx.coroutines.flow.firstOrNull

class ProcessQuestionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_QUESTION_ID = "question_id"
        // TODO fase 2: pindahkan panggilan Gemini ke GitHub Actions proxy, lihat catatan arsitektur
    }

    override suspend fun doWork(): Result {
        val questionId = inputData.getLong(KEY_QUESTION_ID, -1L)
        if (questionId == -1L) return Result.failure()

        val database = GhuslDatabase.getInstance(applicationContext)
        val qnARepository = QnARepository(database.qnADao())
        val geminiService = GeminiService(applicationContext)

        return try {
            // Update status to PROCESSING
            qnARepository.updateStatus(questionId, QnAStatus.PROCESSING)

            // Get the question text from DB
            val qnaEntity = database.qnADao().getQnAById(questionId).firstOrNull()
            val questionText = qnaEntity?.question ?: return Result.failure()
            
            // Call Gemini API
            val answer = geminiService.askQuestion(questionText)

            // Save answer
            qnARepository.updateAnswer(questionId, answer)

            // Show notification
            showNotification(questionText, answer)

            Result.success()
        } catch (e: Exception) {
            qnARepository.updateStatus(questionId, QnAStatus.ERROR)
            Result.failure()
        }
    }

    private fun showNotification(question: String, answer: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationTitle = try {
            applicationContext.getString(R.string.ustadz_notification_title)
        } catch (e: Exception) {
            "Jawaban Ustadz AI"
        }

        val notification = NotificationCompat.Builder(applicationContext, GhuslApplication.CHANNEL_ID_USTADZ)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use app icon - may need to use android.R.drawable.ic_dialog_info as fallback
            .setContentTitle(notificationTitle)
            .setContentText(answer.take(100) + if (answer.length > 100) "..." else "")
            .setStyle(NotificationCompat.BigTextStyle().bigText(answer.take(300)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(questionId.toInt(), notification)
    }

    private val questionId: Long
        get() = inputData.getLong(KEY_QUESTION_ID, -1L)
}
