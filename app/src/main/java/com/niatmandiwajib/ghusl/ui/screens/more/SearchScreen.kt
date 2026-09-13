package com.niatmandiwajib.ghusl.ui.screens.more

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import com.niatmandiwajib.ghusl.ui.components.AdBannerView
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val guideRepository = (application as GhuslApplication).container.guideRepository
    private val userPreferences = (application as GhuslApplication).container.userPreferences

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _allContents = MutableStateFlow<List<GuideContent>>(emptyList())

    val searchResults: StateFlow<List<GuideContent>> = combine(_searchQuery, _allContents) { query, contents ->
        if (query.isBlank()) {
            emptyList()
        } else {
            contents.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true) ||
                it.slides.any { slide -> slide.text.contains(query, ignoreCase = true) || slide.translation.contains(query, ignoreCase = true) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val lang = userPreferences.selectedLanguage.first()
            guideRepository.getGuideContents(lang).collect { result ->
                result.onSuccess { contents ->
                    _allContents.value = contents
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = viewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val context = LocalContext.current
    val adManager = (context.applicationContext as GhuslApplication).adManager

    Scaffold(
        topBar = {
            GhuslTopAppBar(
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() },
                titleContent = {
                    TextField(
                        value = query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text(stringResource(R.string.search_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f)) {
                if (query.isNotEmpty() && results.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.search_empty), color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(results) { content ->
                            ListItem(
                                headlineContent = { Text(content.title) },
                                supportingContent = { 
                                    Text(
                                        text = content.description,
                                        maxLines = 2,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.clickable {
                                    navController.navigate(Screen.SlideShow.createRoute(content.kode))
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
