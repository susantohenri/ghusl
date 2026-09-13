package com.niatmandiwajib.ghusl.ui.screens.guide

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GuideListUiState(
    val isLoading: Boolean = true,
    val contents: List<GuideContent> = emptyList(),
    val error: String? = null
)

class GuideListViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as GhuslApplication).container
    private val guideRepository = container.guideRepository
    private val userPreferences = container.userPreferences

    private val _uiState = MutableStateFlow(GuideListUiState())
    val uiState: StateFlow<GuideListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.selectedLanguage
                .distinctUntilChanged()
                .collectLatest { language ->
                    fetchContents(language = language, forceRefresh = false)
                }
        }
    }

    fun loadGuideContents(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            val language = userPreferences.selectedLanguage.first()
            fetchContents(language = language, forceRefresh = forceRefresh)
        }
    }

    private suspend fun fetchContents(language: String, forceRefresh: Boolean) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        guideRepository.getGuideContents(language, forceRefresh).collect { result ->
            result.onSuccess { contents ->
                _uiState.update { it.copy(isLoading = false, contents = contents) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
