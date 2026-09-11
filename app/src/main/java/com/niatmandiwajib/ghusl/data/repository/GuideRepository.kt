package com.niatmandiwajib.ghusl.data.repository

import com.niatmandiwajib.ghusl.data.remote.api.ApiConstants
import com.niatmandiwajib.ghusl.data.remote.api.GuideApiService
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GuideRepository(private val apiService: GuideApiService) {

    fun getGuideContents(language: String): Flow<Result<List<GuideContent>>> = flow {
        try {
            val dtos = apiService.getGuideContents()
            val contents = dtos.mapNotNull { it.toDomain(language, ApiConstants.MEDIA_BASE_URL) }
            emit(Result.success(contents))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getGuideContent(kode: String, language: String): Flow<Result<GuideContent>> = flow {
        try {
            val dtos = apiService.getGuideContents()
            val content = dtos.firstOrNull { it.kode == kode }?.toDomain(language, ApiConstants.MEDIA_BASE_URL)
            if (content != null) {
                emit(Result.success(content))
            } else {
                emit(Result.failure(Exception("Content not found: $kode")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
