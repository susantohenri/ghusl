package com.niatmandiwajib.ghusl.di

import android.content.Context
import com.niatmandiwajib.ghusl.data.local.database.GhuslDatabase
import com.niatmandiwajib.ghusl.data.local.preferences.UserPreferencesManager
import com.niatmandiwajib.ghusl.data.remote.api.RetrofitClient
import com.niatmandiwajib.ghusl.data.repository.BookmarkRepository
import com.niatmandiwajib.ghusl.data.repository.GuideRepository
import com.niatmandiwajib.ghusl.data.repository.ReadHistoryRepository

class AppContainer(context: Context) {
    private val database = GhuslDatabase.getInstance(context)
    private val apiService = RetrofitClient.getApiService(context)

    val guideRepository = GuideRepository(apiService)
    val bookmarkRepository = BookmarkRepository(database.bookmarkDao())
    val readHistoryRepository = ReadHistoryRepository(database.readHistoryDao())
    val userPreferences = UserPreferencesManager(context)

    // QnA DAO exposed for Tanya Ustadz feature (Fase 6)
    val qnADao = database.qnADao()
}
