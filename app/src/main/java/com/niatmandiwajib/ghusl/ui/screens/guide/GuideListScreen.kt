package com.niatmandiwajib.ghusl.ui.screens.guide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.ui.components.AdBannerView
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import com.niatmandiwajib.ghusl.ui.components.GuideContentCard
import com.niatmandiwajib.ghusl.ui.components.NativeAdCard
import com.niatmandiwajib.ghusl.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideListScreen(
    navController: NavController,
    viewModel: GuideListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val adManager = (context.applicationContext as GhuslApplication).adManager

    Scaffold(
        topBar = {
            GhuslTopAppBar(
                title = stringResource(id = R.string.nav_guide),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.action_search)
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Bookmarks.route) }) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = stringResource(id = R.string.action_bookmark)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error ?: stringResource(id = R.string.guide_error),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadGuideContents() }) {
                        Text(text = stringResource(id = R.string.guide_retry))
                    }
                }
            } else if (uiState.contents.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.guide_empty),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(uiState.contents) { index, content ->
                        GuideContentCard(
                            content = content,
                            onClick = {
                                navController.navigate(Screen.SlideShow.createRoute(content.kode))
                            }
                        )
                        // Insert native ad every 4 items
                        if ((index + 1) % 4 == 0 && index < uiState.contents.size - 1) {
                            NativeAdCard(
                                adUnitId = adManager.getNativeUnitId(),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Adaptive Banner Ad
        AdBannerView(adUnitId = adManager.getBannerUnitId())
    }
}
}

