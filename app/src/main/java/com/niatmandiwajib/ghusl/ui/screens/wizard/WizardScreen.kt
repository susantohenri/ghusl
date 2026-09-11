package com.niatmandiwajib.ghusl.ui.screens.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardScreen(
    navController: NavController,
    viewModel: WizardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_wizard)) },
                navigationIcon = {
                    if (uiState.history.isNotEmpty()) {
                        IconButton(onClick = { viewModel.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)
        ) {
            when {
                uiState.error != null -> {
                    // Error state
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.error ?: "Error", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.restart() }) {
                            Text(stringResource(R.string.guide_retry))
                        }
                    }
                }
                uiState.isFinished && uiState.currentNode != null -> {
                    // Result state - show result inline
                    val node = uiState.currentNode!!
                    val lang = uiState.language
                    WizardResultContent(
                        mustGhusl = node.mustGhusl ?: false,
                        title = node.resultTitle?.get(lang) ?: node.resultTitle?.get("en") ?: "",
                        description = node.resultDescription?.get(lang) ?: node.resultDescription?.get("en") ?: "",
                        dalil = node.resultDalil ?: "",
                        disclaimer = uiState.disclaimer,
                        onRestart = { viewModel.restart() }
                    )
                }
                uiState.currentNode != null -> {
                    // Question state
                    val node = uiState.currentNode!!
                    val lang = uiState.language
                    val questionText = node.question[lang] ?: node.question["en"] ?: "..."

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Step indicator
                        Text(
                            text = stringResource(R.string.wizard_step, uiState.history.size + 1),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Question
                        Text(
                            text = questionText,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        
                        // Yes/No buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.answerNo() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.wizard_no),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Button(
                                onClick = { viewModel.answerYes() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.wizard_yes),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
                else -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun WizardResultContent(
    mustGhusl: Boolean,
    title: String,
    description: String,
    dalil: String,
    disclaimer: String,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Result icon/indicator
        val resultColor = if (mustGhusl) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = resultColor
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (description.isNotBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        if (dalil.isNotBlank()) {
            Text(
                text = dalil,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Disclaimer card
        if (disclaimer.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = disclaimer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        OutlinedButton(onClick = onRestart) {
            Text(stringResource(R.string.wizard_restart))
        }
    }
}
