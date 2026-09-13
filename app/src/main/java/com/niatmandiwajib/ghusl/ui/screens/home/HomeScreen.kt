package com.niatmandiwajib.ghusl.ui.screens.home

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.niatmandiwajib.ghusl.data.local.entity.ReadHistoryEntity
import com.niatmandiwajib.ghusl.ui.components.AdBannerView
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val readHistoryRepository = (application as GhuslApplication).container.readHistoryRepository

    val lastRead: StateFlow<ReadHistoryEntity?> = readHistoryRepository.getLastRead()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val lastRead by viewModel.lastRead.collectAsState()
    val context = LocalContext.current
    val adManager = (context.applicationContext as GhuslApplication).adManager

    Scaffold(
        topBar = {
            GhuslTopAppBar(
                title = stringResource(id = R.string.nav_home),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.action_search)
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.action_settings)
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_greeting),
                    style = MaterialTheme.typography.headlineMedium
                )
            Text(
                text = stringResource(id = R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge
            )

            // Continue reading card
            lastRead?.let { history ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Screen.SlideShow.createRoute(history.contentKode))
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.home_continue_reading),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = history.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = stringResource(id = R.string.history_slide, history.lastSlideIndex + 1),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.Guide.route) }
            ) {
                Text(
                    text = stringResource(id = R.string.home_card_guide),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.Wizard.route) }
            ) {
                Text(
                    text = stringResource(id = R.string.home_card_wizard),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.AskUstadz.route) }
            ) {
                Text(
                    text = stringResource(id = R.string.home_card_ustadz),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Adaptive Banner Ad
        AdBannerView(adUnitId = adManager.getBannerUnitId())
    }
}
}
