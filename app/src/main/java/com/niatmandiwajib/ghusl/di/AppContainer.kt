package com.niatmandiwajib.ghusl.di

import android.content.Context
import com.niatmandiwajib.ghusl.data.local.database.GhuslDatabase
import com.niatmandiwajib.ghusl.data.local.preferences.UserPreferencesManager
import com.niatmandiwajib.ghusl.data.remote.api.GeminiService
import com.niatmandiwajib.ghusl.data.remote.api.RetrofitClient
import com.niatmandiwajib.ghusl.data.repository.*
import com.niatmandiwajib.ghusl.domain.engine.DecisionTreeEngine

class AppContainer(context: Context) {
    private val database = GhuslDatabase.getInstance(context)
    private val apiService = RetrofitClient.getApiService(context)

    val guideRepository = GuideRepository(apiService)
    val bookmarkRepository = BookmarkRepository(database.bookmarkDao())
    val readHistoryRepository = ReadHistoryRepository(database.readHistoryDao())
    val userPreferences = UserPreferencesManager(context)
    val wizardRepository = WizardRepository(DecisionTreeEngine(context))
    val qnADao = database.qnADao()
    val qnARepository = QnARepository(database.qnADao())
    val geminiService = GeminiService(context)
    val adRepository = AdRepository(apiService)
}
