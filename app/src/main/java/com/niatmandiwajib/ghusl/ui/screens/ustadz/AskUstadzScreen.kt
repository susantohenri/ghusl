package com.niatmandiwajib.ghusl.ui.screens.ustadz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.domain.model.QnAItem
import com.niatmandiwajib.ghusl.domain.model.QnAStatus
import com.niatmandiwajib.ghusl.ui.components.AdBannerView
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskUstadzScreen(
    navController: NavController,
    viewModel: AskUstadzViewModel = viewModel()
) {
    val questionText by viewModel.questionText.collectAsState()
    val qnaHistory by viewModel.qnaHistory.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submitSuccess by viewModel.submitSuccess.collectAsState()
    
    val context = LocalContext.current
    val adManager = (context.applicationContext as GhuslApplication).adManager

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(submitSuccess) {
        if (submitSuccess) {
            snackbarHostState.showSnackbar(context.getString(R.string.ustadz_submitted))
            viewModel.resetSubmitSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            GhuslTopAppBar(
                title = stringResource(id = R.string.nav_ask_ustadz),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Faq.route) }) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = stringResource(id = R.string.more_faq)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { viewModel.onQuestionTextChanged(it) },
                    label = { Text(stringResource(id = R.string.ustadz_input_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { viewModel.submitQuestion() },
                    modifier = Modifier.align(Alignment.End),
                    enabled = questionText.isNotBlank() && !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(stringResource(id = R.string.ustadz_submit))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(id = R.string.ustadz_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(id = R.string.ustadz_history_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(qnaHistory) { item ->
                        QnAHistoryItem(item = item, onClick = {
                            navController.navigate("qna_detail/${item.id}")
                        })
                    }
                }
            }
            AdBannerView(adUnitId = adManager.getBannerUnitId())
        }
    }
}

@Composable
fun QnAHistoryItem(item: QnAItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = item.status)
                
                val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                Text(
                    text = dateFormat.format(Date(item.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: QnAStatus) {
    val containerColor = when (status) {
        QnAStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        QnAStatus.PROCESSING -> MaterialTheme.colorScheme.tertiaryContainer
        QnAStatus.ANSWERED -> MaterialTheme.colorScheme.primaryContainer
        QnAStatus.ERROR -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when (status) {
        QnAStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        QnAStatus.PROCESSING -> MaterialTheme.colorScheme.onTertiaryContainer
        QnAStatus.ANSWERED -> MaterialTheme.colorScheme.onPrimaryContainer
        QnAStatus.ERROR -> MaterialTheme.colorScheme.onErrorContainer
    }
    val text = when (status) {
        QnAStatus.PENDING -> stringResource(id = R.string.ustadz_status_pending)
        QnAStatus.PROCESSING -> stringResource(id = R.string.ustadz_status_processing)
        QnAStatus.ANSWERED -> stringResource(id = R.string.ustadz_status_answered)
        QnAStatus.ERROR -> stringResource(id = R.string.ustadz_error)
    }
    
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
