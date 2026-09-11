package com.niatmandiwajib.ghusl.ui.screens.guide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.ui.components.GuideContentCard
import com.niatmandiwajib.ghusl.ui.navigation.Screen

@Composable
fun GuideListScreen(
    navController: NavController,
    viewModel: GuideListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.contents) { content ->
                    GuideContentCard(
                        content = content,
                        onClick = {
                            navController.navigate(Screen.SlideShow.createRoute(content.kode))
                        }
                    )
                }
            }
        }
    }
}
