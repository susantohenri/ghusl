package com.niatmandiwajib.ghusl.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.domain.model.Slide

data class QuickNiatItem(
    val id: String,
    val label: String,
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val audioUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickNiatCard(
    slides: List<Slide>?,
    onNavigateToFullGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val tabJunubLabel = stringResource(R.string.quick_niat_tab_junub)
    val tabHaidLabel = stringResource(R.string.quick_niat_tab_haid)
    val tabNifasLabel = stringResource(R.string.quick_niat_tab_nifas)

    val defaultJunubTitle = stringResource(R.string.default_niat_junub_title)
    val defaultJunubTranslation = stringResource(R.string.default_niat_junub_translation)

    val defaultHaidTitle = stringResource(R.string.default_niat_haid_title)
    val defaultHaidTranslation = stringResource(R.string.default_niat_haid_translation)

    val defaultNifasTitle = stringResource(R.string.default_niat_nifas_title)
    val defaultNifasTranslation = stringResource(R.string.default_niat_nifas_translation)

    val shareMeaningLabel = stringResource(R.string.quick_niat_share_meaning)
    val shareFooter = stringResource(R.string.quick_niat_share_footer)
    val shareChooserTitle = stringResource(R.string.quick_niat_share_title)

    // Baseline items localized dynamically from Android string resources
    val defaultItems = remember(
        tabJunubLabel, tabHaidLabel, tabNifasLabel,
        defaultJunubTitle, defaultHaidTitle, defaultNifasTitle
    ) {
        listOf(
            QuickNiatItem(
                id = "niat-mandi-junub",
                label = tabJunubLabel,
                title = defaultJunubTitle,
                arabic = "نَوَيْتُ الْغُسْلَ لِرَفْعِ الْحَدَثِ الْأَكْبَرِ فَرْضًا لِلّٰهِ تَعَالَى",
                latin = "Nawaitul-ghusla li raf'il-ḥadatsil-akbari farḍan lillāhi ta'ālā",
                translation = defaultJunubTranslation,
                audioUrl = "https://github.com/susantohenri/admob-remote-configs/raw/refs/heads/main/ghusl/konten/niat-mandi-junub.mp3"
            ),
            QuickNiatItem(
                id = "niat-mandi-haid",
                label = tabHaidLabel,
                title = defaultHaidTitle,
                arabic = "نَوَيْتُ الْغُسْلَ لِرَفْعِ حَدَثِ الْحَيْضِ فَرْضًا لِلّٰهِ تَعَالَى",
                latin = "Nawaitul-ghusla li raf'i ḥadatsil-ḥaiḍi farḍan lillāhi ta'ālā",
                translation = defaultHaidTranslation,
                audioUrl = "https://github.com/susantohenri/admob-remote-configs/raw/refs/heads/main/ghusl/konten/niat-mandi-haid.mp3"
            ),
            QuickNiatItem(
                id = "niat-mandi-nifas",
                label = tabNifasLabel,
                title = defaultNifasTitle,
                arabic = "نَوَيْتُ الْغُسْلَ لِرَفْعِ حَدَثِ النِّفَاسِ فَرْضًا لِلّٰهِ تَعَالَى",
                latin = "Nawaitul-ghusla li raf'i ḥadatsin-nifāsi farḍan lillāhi ta'ālā",
                translation = defaultNifasTranslation,
                audioUrl = "https://github.com/susantohenri/admob-remote-configs/raw/refs/heads/main/ghusl/konten/niat-mandi-nifas.mp3"
            )
        )
    }

    // Merge with dynamic slides if available
    val items = remember(slides, defaultItems) {
        if (slides.isNullOrEmpty()) {
            defaultItems
        } else {
            defaultItems.map { defaultItem ->
                val matchingSlide = slides.firstOrNull { it.kode == defaultItem.id }
                if (matchingSlide != null) {
                    defaultItem.copy(
                        title = matchingSlide.title.ifBlank { defaultItem.title },
                        arabic = matchingSlide.arabicText.ifBlank { defaultItem.arabic },
                        latin = matchingSlide.latinText.ifBlank { defaultItem.latin },
                        translation = matchingSlide.translation.ifBlank { defaultItem.translation },
                        audioUrl = matchingSlide.audioUrl.ifBlank { defaultItem.audioUrl }
                    )
                } else {
                    defaultItem
                }
            }
        }
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val currentItem = items.getOrElse(selectedIndex) { items.first() }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.quick_niat_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AssistChip(
                    onClick = onNavigateToFullGuide,
                    label = { Text(stringResource(R.string.quick_niat_full_guide)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            // Tabs / Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEachIndexed { index, item ->
                    FilterChip(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(item.label) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Animated content based on tab
            AnimatedContent(
                targetState = currentItem,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "QuickNiatTabContent"
            ) { item ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Arabic Text
                    Text(
                        text = item.arabic,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            lineHeight = 42.sp,
                            textDirection = TextDirection.Rtl
                        ),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Latin Transliteration
                    Text(
                        text = item.latin,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Translation
                    Text(
                        text = item.translation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )

                    // Audio Player
                    if (item.audioUrl.isNotBlank()) {
                        AudioPlayerBar(audioUrl = item.audioUrl)
                    }

                    // Share button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${item.title}\n\n${item.arabic}\n\n\"${item.latin}\"\n\n$shareMeaningLabel\n${item.translation}\n\n$shareFooter"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, shareChooserTitle))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.action_share),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_share))
                        }
                    }
                }
            }
        }
    }
}
