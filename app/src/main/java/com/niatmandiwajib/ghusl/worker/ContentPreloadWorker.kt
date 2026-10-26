package com.niatmandiwajib.ghusl.worker

import android.content.Context
import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheWriter
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.data.remote.api.ApiConstants
import com.niatmandiwajib.ghusl.media.AudioCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@UnstableApi
class ContentPreloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        val PRIORITY_AUDIO_FILES = listOf(
            "niat-mandi-wajib.mp3",
            "niat-mandi-junub.mp3",
            "niat-mandi-haid.mp3",
            "niat-mandi-nifas.mp3"
        )
        val PRIORITY_IMAGE_FILES = listOf(
            "niat-mandi-wajib.webp",
            "niat-mandi-junub.webp",
            "niat-mandi-haid.webp",
            "niat-mandi-nifas.webp"
        )
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val app = applicationContext as? GhuslApplication ?: return@withContext Result.success()

        // 1. Sync Guide data to Room
        try {
            app.container.guideRepository.syncFromRemote()
        } catch (e: Exception) {
            // Non-fatal, offline fallback is active
        }

        // 2. Pre-cache priority audio files into Media3 cache
        try {
            val cacheDataSource = AudioCacheManager.getCachedDataSourceFactory(applicationContext).createDataSource()
            for (audioFile in PRIORITY_AUDIO_FILES) {
                try {
                    val uri = Uri.parse(ApiConstants.MEDIA_BASE_URL + audioFile)
                    val dataSpec = DataSpec(uri)
                    val writer = CacheWriter(cacheDataSource, dataSpec, null, null)
                    writer.cache()
                } catch (e: Exception) {
                    // Ignore failure for individual audio file
                }
            }
        } catch (e: Exception) {
            // Media3 cache init failed
        }

        // 3. Pre-cache priority images into Coil disk cache
        try {
            val imageLoader = applicationContext.imageLoader
            for (imageFile in PRIORITY_IMAGE_FILES) {
                try {
                    val request = ImageRequest.Builder(applicationContext)
                        .data(ApiConstants.MEDIA_BASE_URL + imageFile)
                        .build()
                    imageLoader.enqueue(request)
                } catch (e: Exception) {
                    // Ignore failure
                }
            }
        } catch (e: Exception) {
            // Coil cache init failed
        }

        Result.success()
    }
}
