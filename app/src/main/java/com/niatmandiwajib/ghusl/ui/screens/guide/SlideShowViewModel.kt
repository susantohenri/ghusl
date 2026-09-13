package com.niatmandiwajib.ghusl.ui.screens.guide

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SlideShowUiState(
    val isLoading: Boolean = true,
    val content: GuideContent? = null,
    val currentSlideIndex: Int = 0,
    val isBookmarked: Boolean = false,
    val error: String? = null
)

class SlideShowViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val container = (application as GhuslApplication).container
    private val guideRepository = container.guideRepository
    private val bookmarkRepository = container.bookmarkRepository
    private val readHistoryRepository = container.readHistoryRepository
    private val userPreferences = container.userPreferences

    private val contentId: String = checkNotNull(savedStateHandle["contentId"])

    private val _uiState = MutableStateFlow(SlideShowUiState())
    val uiState: StateFlow<SlideShowUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.selectedLanguage
                .distinctUntilChanged()
                .collectLatest { language ->
                    loadContent(language)
                }
        }
        observeBookmarkStatus()
    }

    private fun loadContent(language: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            guideRepository.getGuideContent(contentId, language).collect { result ->
                result.onSuccess { content ->
                    _uiState.update { it.copy(isLoading = false, content = content) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    private fun observeBookmarkStatus() {
        viewModelScope.launch {
            bookmarkRepository.isBookmarked(contentId).collect { isBookmarked ->
                _uiState.update { it.copy(isBookmarked = isBookmarked) }
            }
        }
    }

    fun onSlideChanged(index: Int) {
        _uiState.update { it.copy(currentSlideIndex = index) }
        val title = uiState.value.content?.title ?: ""
        viewModelScope.launch {
            readHistoryRepository.updateHistory(contentId, index, title)
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val title = uiState.value.content?.title ?: ""
            if (uiState.value.isBookmarked) {
                bookmarkRepository.removeBookmark(contentId)
            } else {
                bookmarkRepository.addBookmark(contentId, title)
            }
        }
    }
}
