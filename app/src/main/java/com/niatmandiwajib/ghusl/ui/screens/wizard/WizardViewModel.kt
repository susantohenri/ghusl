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
    // WizardRepository will be lazily accessed
    // Note: WizardRepository is added to AppContainer after this feature is done

    private val _uiState = MutableStateFlow(WizardUiState())
    val uiState: StateFlow<WizardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val language = userPreferences.selectedLanguage.first()
            try {
                val engine = com.niatmandiwajib.ghusl.domain.engine.DecisionTreeEngine(application)
                val startNode = engine.getStartNode()
                val disclaimer = engine.getDisclaimer(language)
                _uiState.update {
                    it.copy(
                        currentNode = startNode,
                        language = language,
                        disclaimer = disclaimer
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun answerYes() {
        val current = _uiState.value.currentNode ?: return
        val engine = try {
            com.niatmandiwajib.ghusl.domain.engine.DecisionTreeEngine(getApplication())
        } catch (e: Exception) { return }
        
        val nextNode = engine.answerYes(current)
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
        val engine = try {
            com.niatmandiwajib.ghusl.domain.engine.DecisionTreeEngine(getApplication())
        } catch (e: Exception) { return }
        
        val nextNode = engine.answerNo(current)
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
                val engine = com.niatmandiwajib.ghusl.domain.engine.DecisionTreeEngine(getApplication())
                _uiState.update {
                    it.copy(
                        currentNode = engine.getStartNode(),
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
