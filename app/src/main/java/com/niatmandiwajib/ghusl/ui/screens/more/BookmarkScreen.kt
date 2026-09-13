package com.niatmandiwajib.ghusl.ui.screens.more

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.niatmandiwajib.ghusl.data.local.entity.BookmarkEntity
import com.niatmandiwajib.ghusl.ui.components.AdBannerView
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private val bookmarkRepository = (application as GhuslApplication).container.bookmarkRepository

    val bookmarks: StateFlow<List<BookmarkEntity>> = bookmarkRepository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeBookmark(contentKode: String, slideKode: String?) {
        viewModelScope.launch {
            bookmarkRepository.removeBookmark(contentKode)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    navController: NavController,
    viewModel: BookmarkViewModel = viewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val context = LocalContext.current
    val adManager = (context.applicationContext as GhuslApplication).adManager

    Scaffold(
        topBar = {
            GhuslTopAppBar(
                title = stringResource(R.string.more_bookmarks),
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f)) {
                if (bookmarks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.bookmarks_empty), color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(bookmarks) { bookmark ->
                            ListItem(
                                headlineContent = { Text(bookmark.title) },
                                supportingContent = { Text(dateFormat.format(Date(bookmark.bookmarkedAt))) },
                                trailingContent = {
                                    IconButton(onClick = { viewModel.removeBookmark(bookmark.contentKode, bookmark.slideKode) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                                    }
                                },
                                modifier = Modifier.clickable {
                                    navController.navigate(Screen.SlideShow.createRoute(bookmark.contentKode))
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
            AdBannerView(adUnitId = adManager.getBannerUnitId())
        }
    }
}
