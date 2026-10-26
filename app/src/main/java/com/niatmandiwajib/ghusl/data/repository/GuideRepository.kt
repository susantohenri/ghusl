package com.niatmandiwajib.ghusl.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niatmandiwajib.ghusl.data.local.dao.GuideDao
import com.niatmandiwajib.ghusl.data.local.entity.GuideContentEntity
import com.niatmandiwajib.ghusl.data.remote.api.ApiConstants
import com.niatmandiwajib.ghusl.data.remote.api.GuideApiService
import com.niatmandiwajib.ghusl.data.remote.dto.GuideContentDto
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GuideRepository(
    private val context: Context,
    private val apiService: GuideApiService,
    private val guideDao: GuideDao
) {
    @Volatile
    private var cachedDtos: List<GuideContentDto>? = null
    private val gson = Gson()

    suspend fun syncFromRemote(): Result<List<GuideContentDto>> = withContext(Dispatchers.IO) {
        try {
            val remoteDtos = apiService.getGuideContents()
            cachedDtos = remoteDtos
            // Save to Room Database
            val entities = remoteDtos.map { dto ->
                GuideContentEntity(
                    kode = dto.kode,
                    jsonPayload = gson.toJson(dto),
                    lastUpdated = System.currentTimeMillis()
                )
            }
            guideDao.insertGuides(entities)
            Result.success(remoteDtos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getLocalOrSeedDtos(): List<GuideContentDto> = withContext(Dispatchers.IO) {
        // 1. Try from memory
        cachedDtos?.let { return@withContext it }

        // 2. Try from Room Database
        val localEntities = guideDao.getAllGuidesSync()
        if (localEntities.isNotEmpty()) {
            val dtos = localEntities.mapNotNull { entity ->
                try {
                    gson.fromJson(entity.jsonPayload, GuideContentDto::class.java)
                } catch (e: Exception) {
                    null
                }
            }
            if (dtos.isNotEmpty()) {
                cachedDtos = dtos
                return@withContext dtos
            }
        }

        // 3. Fallback to seed in assets
        try {
            context.assets.open("data_seed.json").use { stream ->
                val jsonString = stream.bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<GuideContentDto>>() {}.type
                val seedDtos: List<GuideContentDto> = gson.fromJson(jsonString, type)
                if (seedDtos.isNotEmpty()) {
                    cachedDtos = seedDtos
                    // Seed into Room
                    val entities = seedDtos.map { dto ->
                        GuideContentEntity(
                            kode = dto.kode,
                            jsonPayload = gson.toJson(dto),
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                    guideDao.insertGuides(entities)
                    return@withContext seedDtos
                }
            }
        } catch (e: Exception) {
            // Seed read failed
        }

        emptyList()
    }

    fun getGuideContents(language: String, forceRefresh: Boolean = false): Flow<Result<List<GuideContent>>> = flow {
        if (forceRefresh) {
            cachedDtos = null
        }

        // Emit local/cached/seed first for 0 ms immediate response
        val localDtos = getLocalOrSeedDtos()
        if (localDtos.isNotEmpty()) {
            val contents = localDtos.mapNotNull { it.toDomain(language, ApiConstants.MEDIA_BASE_URL) }
            emit(Result.success(contents))
        }

        // If local was empty OR we should check remote, fetch from GitHub
        val shouldFetchRemote = forceRefresh || localDtos.isEmpty() || cachedDtos == null
        if (shouldFetchRemote) {
            val syncResult = syncFromRemote()
            syncResult.onSuccess { freshDtos ->
                val freshContents = freshDtos.mapNotNull { it.toDomain(language, ApiConstants.MEDIA_BASE_URL) }
                emit(Result.success(freshContents))
            }.onFailure { error ->
                if (localDtos.isEmpty()) {
                    emit(Result.failure(error))
                }
                // If localDtos was not empty, user already received valid content; do not fail.
            }
        }
    }

    fun getGuideContent(kode: String, language: String, forceRefresh: Boolean = false): Flow<Result<GuideContent>> = flow {
        if (forceRefresh) {
            cachedDtos = null
        }

        val localDtos = getLocalOrSeedDtos()
        val localContent = localDtos.firstOrNull { it.kode == kode }?.toDomain(language, ApiConstants.MEDIA_BASE_URL)
        if (localContent != null) {
            emit(Result.success(localContent))
        }

        val shouldFetchRemote = forceRefresh || localContent == null
        if (shouldFetchRemote) {
            val syncResult = syncFromRemote()
            syncResult.onSuccess { freshDtos ->
                val fresh = freshDtos.firstOrNull { it.kode == kode }?.toDomain(language, ApiConstants.MEDIA_BASE_URL)
                if (fresh != null) {
                    emit(Result.success(fresh))
                } else if (localContent == null) {
                    emit(Result.failure(Exception("Content not found: $kode")))
                }
            }.onFailure { error ->
                if (localContent == null) {
                    emit(Result.failure(error))
                }
            }
        }
    }
}
