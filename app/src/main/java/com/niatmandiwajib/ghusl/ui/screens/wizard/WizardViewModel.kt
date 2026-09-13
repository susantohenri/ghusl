package com.niatmandiwajib.ghusl.ui.screens.wizard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.domain.model.WizardNode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WizardUiState(
    val currentNode: WizardNode? = null,
    val history: List<WizardNode> = emptyList(), // for back navigation
    val language: String = "en",
    val disclaimer: String = "",
    val isFinished: Boolean = false,
    val error: String? = null
)

class WizardViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as GhuslApplication).container
    private val userPreferences = container.userPreferences
    private val wizardRepository = container.wizardRepository

    private val _uiState = MutableStateFlow(WizardUiState())
    val uiState: StateFlow<WizardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.selectedLanguage
                .distinctUntilChanged()
                .collectLatest { language ->
                    try {
                        val disclaimer = wizardRepository.getDisclaimer(language)
                        _uiState.update { current ->
                            current.copy(
                                currentNode = current.currentNode ?: wizardRepository.getStartNode(),
                                language = language,
                                disclaimer = disclaimer
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(error = e.message) }
                    }
                }
        }
    }

    fun answerYes() {
        val current = _uiState.value.currentNode ?: return
        val nextNode = wizardRepository.answerYes(current)
        if (nextNode != null) {
            _uiState.update {
                it.copy(
                    currentNode = nextNode,
                    history = it.history + current,
                    isFinished = nextNode.isResult
                )
            }
        }
    }

    fun answerNo() {
        val current = _uiState.value.currentNode ?: return
        val nextNode = wizardRepository.answerNo(current)
        if (nextNode != null) {
            _uiState.update {
                it.copy(
                    currentNode = nextNode,
                    history = it.history + current,
                    isFinished = nextNode.isResult
                )
            }
        }
    }

    fun goBack() {
        val history = _uiState.value.history
        if (history.isNotEmpty()) {
            val previous = history.last()
            _uiState.update {
                it.copy(
                    currentNode = previous,
                    history = history.dropLast(1),
                    isFinished = false
                )
            }
        }
    }

    fun restart() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        currentNode = wizardRepository.getStartNode(),
                        history = emptyList(),
                        isFinished = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
