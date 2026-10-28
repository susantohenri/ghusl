package com.niatmandiwajib.ghusl.ui.screens.home

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.GhuslApplication
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.data.local.entity.ReadHistoryEntity
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import com.niatmandiwajib.ghusl.ui.components.AdBannerView
import com.niatmandiwajib.ghusl.ui.components.GhuslTopAppBar
import com.niatmandiwajib.ghusl.ui.components.HomeFeatureCard
import com.niatmandiwajib.ghusl.ui.components.QuickNiatCard
import com.niatmandiwajib.ghusl.ui.components.QuickStepsCarousel
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import com.niatmandiwajib.ghusl.ui.theme.GhuslGreenPrimary
import com.niatmandiwajib.ghusl.ui.theme.GhuslGreenPrimaryVariant
import com.niatmandiwajib.ghusl.ui.theme.IslamicPatternBackground
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as GhuslApplication).container
    private val readHistoryRepository = container.readHistoryRepository
    private val guideRepository = container.guideRepository
    private val userPreferences = container.userPreferences

    val lastRead: StateFlow<ReadHistoryEntity?> = readHistoryRepository.getLastRead()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _quickNiatGuide = kotlinx.coroutines.flow.MutableStateFlow<GuideContent?>(null)
    val quickNiatGuide: StateFlow<GuideContent?> = _quickNiatGuide

    init {
        loadQuickNiat()
    }

    private fun loadQuickNiat() {
        viewModelScope.launch {
            userPreferences.selectedLanguage.collect { lang ->
                guideRepository.getGuideContent("niat-doa-mandi-wajib", lang).collect { result ->
                    result.onSuccess { content ->
                        _quickNiatGuide.value = content
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val lastRead by viewModel.lastRead.collectAsState()
    val quickNiatGuide by viewModel.quickNiatGuide.collectAsState()
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High-performance GPU-rendered Islamic Geometric Watermark
            IslamicPatternBackground(
                color = MaterialTheme.colorScheme.primary,
                alpha = 0.04f,
                cellSize = 120f
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Hero Islamic Greeting Banner
                    val cal = remember { Calendar.getInstance() }
                    val isFridayOrEve = remember {
                        val day = cal.get(Calendar.DAY_OF_WEEK)
                        val hour = cal.get(Calendar.HOUR_OF_DAY)
                        (day == Calendar.THURSDAY && hour >= 16) || (day == Calendar.FRIDAY && hour <= 14)
                    }
                    val currentHour = remember { cal.get(Calendar.HOUR_OF_DAY) }
                    val timeGreetingRes = remember(currentHour) {
                        when (currentHour) {
                            in 4..10 -> R.string.greeting_morning
                            in 11..14 -> R.string.greeting_afternoon
                            in 15..18 -> R.string.greeting_evening
                            else -> R.string.greeting_night
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFFC5A059).copy(alpha = 0.35f)),
                        tonalElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(GhuslGreenPrimaryVariant, GhuslGreenPrimary)
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${stringResource(id = R.string.home_greeting)}, ${stringResource(id = timeGreetingRes)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (isFridayOrEve) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFFFD54F),
                                            contentColor = Color(0xFF3E2723)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = stringResource(R.string.badge_friday_sunnah),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = stringResource(id = R.string.home_hero_blessing),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.92f)
                                )
                            }
                        }
                    }

                    // 2. Quick Niat Card (Instant value at 0 seconds)
                    QuickNiatCard(
                        slides = quickNiatGuide?.slides,
                        onNavigateToFullGuide = {
                            navController.navigate(Screen.SlideShow.createRoute("niat-doa-mandi-wajib"))
                        }
                    )

                    // 3. 5 Quick Steps Carousel (Links to exact slide in comprehensive guide)
                    QuickStepsCarousel(
                        onStepClick = { stepIndex ->
                            navController.navigate(
                                Screen.SlideShow.createRoute(
                                    contentId = "panduan-lengkap-mandi-wajib",
                                    initialSlide = stepIndex
                                )
                            )
                        }
                    )

                    // 4. Continue reading card (if any)
                    lastRead?.let { history ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Screen.SlideShow.createRoute(history.contentKode))
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
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
                                        fontWeight = FontWeight.SemiBold,
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

                    // 5. Main Features Section
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = stringResource(R.string.home_section_menu),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        HomeFeatureCard(
                            title = stringResource(id = R.string.home_card_guide),
                            description = stringResource(id = R.string.home_card_guide_desc),
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = { navController.navigate(Screen.Guide.route) }
                        )

                        HomeFeatureCard(
                            title = stringResource(id = R.string.home_card_wizard),
                            description = stringResource(id = R.string.home_card_wizard_desc),
                            icon = Icons.AutoMirrored.Filled.FactCheck,
                            iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            badgeText = stringResource(id = R.string.badge_interactive),
                            onClick = { navController.navigate(Screen.Wizard.route) }
                        )

                        HomeFeatureCard(
                            title = stringResource(id = R.string.home_card_ustadz),
                            description = stringResource(id = R.string.home_card_ustadz_desc),
                            icon = Icons.Default.ChatBubble,
                            iconContainerColor = Color(0xFFE8DEF8),
                            iconColor = Color(0xFF6750A4),
                            badgeText = stringResource(id = R.string.badge_private),
                            onClick = { navController.navigate(Screen.AskUstadz.route) }
                        )
                    }

                    // 6. Dynamic Purification Wisdom ("Tahukah Anda?") Card
                    val tipsList = remember {
                        listOf(
                            R.string.home_tip_1,
                            R.string.home_tip_2,
                            R.string.home_tip_3,
                            R.string.home_tip_4,
                            R.string.home_tip_5,
                            R.string.home_tip_6,
                            R.string.home_tip_7
                        )
                    }
                    val defaultTipIndex = remember {
                        Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % tipsList.size
                    }
                    var currentTipIndex by remember { mutableIntStateOf(defaultTipIndex) }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFFF8E1),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = Color(0xFFF57F17),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = stringResource(R.string.home_tip_title),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "${currentTipIndex + 1}/${tipsList.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        currentTipIndex = (currentTipIndex + 1) % tipsList.size
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = stringResource(R.string.action_next_tip),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            AnimatedContent(
                                targetState = currentTipIndex,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "WisdomTipCrossfade"
                            ) { index ->
                                Text(
                                    text = stringResource(tipsList[index]),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                // Adaptive Banner Ad
                AdBannerView(adUnitId = adManager.getBannerUnitId())
            }
        }
    }
}
