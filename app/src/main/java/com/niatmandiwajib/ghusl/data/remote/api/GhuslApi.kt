package com.niatmandiwajib.ghusl.data.remote.api

import android.content.Context
import com.niatmandiwajib.ghusl.data.remote.dto.AdConfigDto
import com.niatmandiwajib.ghusl.data.remote.dto.GuideContentDto
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.concurrent.TimeUnit

object ApiConstants {
    const val BASE_URL = "https://raw.githubusercontent.com/"
    const val MEDIA_BASE_URL = "https://github.com/susantohenri/admob-remote-configs/raw/refs/heads/main/ghusl/konten/"
    const val GUIDE_CONTENT_PATH = "susantohenri/admob-remote-configs/refs/heads/main/ghusl/konten/data.json"
    const val ADS_CONFIG_PATH = "susantohenri/admob-remote-configs/refs/heads/main/ghusl/ads_config.json"
}

interface GuideApiService {
    @GET(ApiConstants.GUIDE_CONTENT_PATH)
    suspend fun getGuideContents(): List<GuideContentDto>

    @GET(ApiConstants.ADS_CONFIG_PATH)
    suspend fun getAdConfig(): AdConfigDto
}

object RetrofitClient {
    private var instance: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        return instance ?: synchronized(this) {
            val cacheDir = File(context.cacheDir, "http_cache")
            val cache = Cache(cacheDir, 10L * 1024 * 1024) // 10 MB

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { instance = it }
        }
    }

    fun getApiService(context: Context): GuideApiService {
        return getInstance(context).create(GuideApiService::class.java)
    }
}
