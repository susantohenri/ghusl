package com.niatmandiwajib.ghusl.ui.screens.guide

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.ui.components.AudioPlayerBar
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SlideShowScreen(
    contentId: String,
    navController: NavController,
    viewModel: SlideShowViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val adManager = (context.applicationContext as GhuslApplication).adManager
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            GhuslTopAppBar(
                title = uiState.content?.title ?: "",
                canNavigateBack = true,
                onBackClick = {
                    val activity = context as? android.app.Activity
                    if (activity != null) {
                        adManager.showInterstitialIfReady(activity)
                    }
                    navController.popBackStack()
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = stringResource(id = R.string.action_bookmark)
                        )
                    }
                    IconButton(onClick = {
                        val slide = uiState.content?.slides?.getOrNull(uiState.currentSlideIndex)
                        if (slide != null) {
                            val shareText = "${slide.title}\n\n${slide.text}\n\n${slide.arabicText}\n${slide.translation}\n\nSource: ${slide.source}"
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.slide_share_title))
                            context.startActivity(shareIntent)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(id = R.string.action_share)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: stringResource(id = R.string.guide_error),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.content != null) {
                val slides = uiState.content!!.slides
                val pagerState = rememberPagerState(pageCount = { slides.size })
                
                LaunchedEffect(pagerState.currentPage) {
                    viewModel.onSlideChanged(pagerState.currentPage)
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        val slide = slides[page]
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                        ) {
                            Text(
                                text = slide.title,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (slide.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(slide.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = slide.title,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            if (slide.arabicText.isNotBlank()) {
                                Text(
                                    text = slide.arabicText,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDirection = TextDirection.Rtl,
                                        fontSize = 24.sp
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (slide.latinText.isNotBlank()) {
                                Text(
                                    text = slide.latinText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (slide.translation.isNotBlank()) {
                                Text(
                                    text = slide.translation,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            if (slide.text.isNotBlank()) {
                                Text(
                                    text = slide.text,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            if (slide.source.isNotBlank()) {
                                Text(
                                    text = slide.source,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                            }
                        }
                    }
                    
                    // Bottom Bar with Audio & Indicator
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            val currentAudio = slides[pagerState.currentPage].audioUrl
                            if (currentAudio.isNotBlank()) {
                                AudioPlayerBar(audioUrl = currentAudio)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (pagerState.currentPage > 0) {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                            }
                                        }
                                    },
                                    enabled = pagerState.currentPage > 0
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(id = R.string.action_back)
                                    )
                                }

                                Text(
                                    text = stringResource(id = R.string.slide_of, pagerState.currentPage + 1, slides.size),
                                    style = MaterialTheme.typography.labelLarge
                                )

                                IconButton(
                                    onClick = {
                                        if (pagerState.currentPage < slides.size - 1) {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            }
                                        }
                                    },
                                    enabled = pagerState.currentPage < slides.size - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
