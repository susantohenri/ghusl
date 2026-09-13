package com.niatmandiwajib.ghusl.ui.screens.ustadz

import android.app.Application
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.data.repository.QnARepository
import com.niatmandiwajib.ghusl.domain.model.QnAItem
import com.niatmandiwajib.ghusl.domain.model.QnAStatus
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QnADetailViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as GhuslApplication).container
    private val qnARepository = QnARepository(container.qnADao)
    
    private val _qnaItem = MutableStateFlow<QnAItem?>(null)
    val qnaItem: StateFlow<QnAItem?> = _qnaItem
    
    fun loadQuestion(questionId: Long) {
        viewModelScope.launch {
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
            GhuslTopAppBar(
                title = stringResource(R.string.ustadz_detail_title),
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() },
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
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = stringResource(id = R.string.action_share)
                                )
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Question
                Text(
                    text = stringResource(R.string.ustadz_question_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.question, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Answer
                Text(
                    text = stringResource(R.string.ustadz_answer_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                when (item.status) {
                    QnAStatus.ANSWERED -> {
                        Text(item.answer ?: "", style = MaterialTheme.typography.bodyLarge)
                    }
                    QnAStatus.PENDING, QnAStatus.PROCESSING -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.ustadz_processing),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    QnAStatus.ERROR -> {
                        Text(
                            text = stringResource(R.string.ustadz_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Disclaimer
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = stringResource(R.string.ustadz_disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
