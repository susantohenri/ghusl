package com.niatmandiwajib.ghusl.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@androidx.media3.common.util.UnstableApi
@Composable
fun AudioPlayerBar(
    audioUrl: String,
    modifier: Modifier = Modifier
) {
    if (audioUrl.isBlank()) return

    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val exoPlayer = remember(audioUrl) {
        val cachedDataSourceFactory = com.niatmandiwajib.ghusl.media.AudioCacheManager.getCachedDataSourceFactory(context)
        val mediaSource = androidx.media3.exoplayer.source.ProgressiveMediaSource.Factory(cachedDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(audioUrl)))
        ExoPlayer.Builder(context).build().apply {
            setMediaSource(mediaSource)
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        isPlaying = false
                        seekTo(0)
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
            })
        }
    }

    // Update progress periodically
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val duration = exoPlayer.duration.takeIf { it > 0 } ?: 1L
            progress = exoPlayer.currentPosition.toFloat() / duration.toFloat()
            kotlinx.coroutines.delay(200L)
        }
    }

    DisposableEffect(audioUrl) {
        onDispose {
            exoPlayer.release()
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}
