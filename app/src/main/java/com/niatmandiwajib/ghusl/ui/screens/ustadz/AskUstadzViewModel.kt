package com.niatmandiwajib.ghusl.ui.screens.ustadz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.niatmandiwajib.ghusl.BuildConfig
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.data.repository.QnARepository
import com.niatmandiwajib.ghusl.domain.model.QnAItem
import com.niatmandiwajib.ghusl.worker.ProcessQuestionWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class AskUstadzViewModel(application: Application) : AndroidViewModel(application) {
    
    private val container = (application as GhuslApplication).container
    private val qnARepository = QnARepository(container.qnADao)
    
    private val _questionText = MutableStateFlow("")
    val questionText: StateFlow<String> = _questionText.asStateFlow()
    
    private val _qnaHistory = MutableStateFlow<List<QnAItem>>(emptyList())
    val qnaHistory: StateFlow<List<QnAItem>> = _qnaHistory.asStateFlow()
    
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()
    
    private val _submitSuccess = MutableStateFlow(false)
    val submitSuccess: StateFlow<Boolean> = _submitSuccess.asStateFlow()
    
    init {
        viewModelScope.launch {
            qnARepository.getAllQnA().collect { history ->
                _qnaHistory.value = history
            }
        }
    }
    
    fun onQuestionTextChanged(newText: String) {
        _questionText.value = newText
    }
    
    fun resetSubmitSuccess() {
        _submitSuccess.value = false
    }
    
    fun submitQuestion() {
        if (_questionText.value.isBlank()) return
        
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val questionId = qnARepository.submitQuestion(_questionText.value)
                
                val context = getApplication<Application>().applicationContext
                val delay = if (BuildConfig.DEBUG) 0L else (2 * 60 * 60 * 1000L) + (Random.nextLong(2 * 60 * 60 * 1000L)) // 2-4 hours
                
                val workRequest = OneTimeWorkRequestBuilder<ProcessQuestionWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(ProcessQuestionWorker.KEY_QUESTION_ID to questionId))
                    .build()
                    
                WorkManager.getInstance(context).enqueue(workRequest)
                
                _questionText.value = ""
                _submitSuccess.value = true
            } catch (e: Exception) {
                // Handle error if needed
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
