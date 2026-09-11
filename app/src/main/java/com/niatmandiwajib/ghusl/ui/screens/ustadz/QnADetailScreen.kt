package com.niatmandiwajib.ghusl.ui.screens.ustadz

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.domain.model.QnAStatus
import kotlinx.coroutines.launch

// ViewModel for detail screen
class QnADetailViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {
    private val container = (application as com.niatmandiwajib.ghusl.GhuslApplication).container
    // QnARepository needs to be added to AppContainer - for now access via qnADao
    private val qnARepository = com.niatmandiwajib.ghusl.data.repository.QnARepository(container.qnADao)
    
    private val _qnaItem = kotlinx.coroutines.flow.MutableStateFlow<com.niatmandiwajib.ghusl.domain.model.QnAItem?>(null)
    val qnaItem: kotlinx.coroutines.flow.StateFlow<com.niatmandiwajib.ghusl.domain.model.QnAItem?> = _qnaItem
    
    fun loadQuestion(questionId: Long) {
        androidx.lifecycle.viewModelScope.launch {
            qnARepository.getQnAById(questionId).collect { item ->
                _qnaItem.value = item
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnADetailScreen(
    questionId: Long,
    navController: NavController,
    viewModel: QnADetailViewModel = viewModel()
) {
    val qnaItem by viewModel.qnaItem.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(questionId) {
        viewModel.loadQuestion(questionId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(try { stringResource(R.string.ustadz_detail_title) } catch(e: Exception) { "Tanya Ustadz AI" }) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    qnaItem?.let { item ->
                        if (item.answer != null) {
                            IconButton(onClick = {
                                val shareText = "Q: ${item.question}\n\nA: ${item.answer}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                            }) {
                                Icon(Icons.Filled.Share, contentDescription = "Share")
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        qnaItem?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Question
                Text(try { stringResource(R.string.ustadz_question_label) } catch(e: Exception) { "Pertanyaan" }, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.question, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Answer
                Text(try { stringResource(R.string.ustadz_answer_label) } catch(e: Exception) { "Jawaban" }, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                
                when (item.status) {
                    QnAStatus.ANSWERED -> {
                        Text(item.answer ?: "", style = MaterialTheme.typography.bodyLarge)
                    }
                    QnAStatus.PENDING, QnAStatus.PROCESSING -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(try { stringResource(R.string.ustadz_processing) } catch(e: Exception) { "Sedang memproses..." }, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    QnAStatus.ERROR -> {
                        Text(try { stringResource(R.string.ustadz_error) } catch(e: Exception) { "Gagal memproses jawaban" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Disclaimer
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = try { stringResource(R.string.ustadz_disclaimer) } catch(e: Exception) { "Jawaban dihasilkan oleh AI, bersifat edukasi umum seputar mandi wajib, dan bukan fatwa resmi. Untuk kasus personal yang kompleks, silakan konsultasi langsung dengan ustadz, ustadzah, atau lembaga fatwa terpercaya." },
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
