package com.niatmandiwajib.ghusl.ui.screens.more

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.data.local.entity.ReadHistoryEntity
import com.niatmandiwajib.ghusl.domain.model.QnAItem
import com.niatmandiwajib.ghusl.domain.model.QnAStatus
import com.niatmandiwajib.ghusl.ui.components.AdBannerView
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as GhuslApplication).container
    private val historyRepository = container.readHistoryRepository
    private val qnARepository = container.qnARepository

    val readHistory: StateFlow<List<ReadHistoryEntity>> = historyRepository.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val qnaHistory: StateFlow<List<QnAItem>> = qnARepository.getAllQnA()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel()
) {
    val readHistory by viewModel.readHistory.collectAsState()
    val qnaHistory by viewModel.qnaHistory.collectAsState()
    val context = LocalContext.current
    val adManager = (context.applicationContext as GhuslApplication).adManager
    val tabTitles = listOf(
        stringResource(R.string.history_tab_reading),
        stringResource(R.string.history_tab_qna)
    )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GhuslTopAppBar(
                title = stringResource(R.string.more_history),
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> ReadHistoryTab(readHistory, navController)
                    1 -> QnAHistoryTab(qnaHistory, navController)
                }
            }
        }
    }
}

@Composable
fun ReadHistoryTab(history: List<ReadHistoryEntity>, navController: NavController) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.history_empty), color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history) { item ->
                ListItem(
                    headlineContent = { Text(item.title) },
                    supportingContent = {
                        Column {
                            Text(stringResource(R.string.history_slide, item.lastSlideIndex + 1))
                            Text(dateFormat.format(Date(item.lastReadAt)), style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.SlideShow.createRoute(item.contentKode))
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun QnAHistoryTab(qnaHistory: List<QnAItem>, navController: NavController) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    if (qnaHistory.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.ustadz_history_empty), color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(qnaHistory) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = item.question,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = {
                        Column {
                            Text(
                                text = when (item.status) {
                                    QnAStatus.PENDING -> stringResource(R.string.ustadz_status_pending)
                                    QnAStatus.PROCESSING -> stringResource(R.string.ustadz_status_processing)
                                    QnAStatus.ANSWERED -> stringResource(R.string.ustadz_status_answered)
                                    QnAStatus.ERROR -> stringResource(R.string.ustadz_error)
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = dateFormat.format(Date(item.createdAt)),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    modifier = Modifier.clickable {
                        navController.navigate("qna_detail/${item.id}")
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
