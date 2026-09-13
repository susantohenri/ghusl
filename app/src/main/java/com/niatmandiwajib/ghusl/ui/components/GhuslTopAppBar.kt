@file:OptIn(ExperimentalMaterial3Api::class)

package com.niatmandiwajib.ghusl.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niatmandiwajib.ghusl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhuslTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    elevation: Dp = 3.dp,
    colors: TopAppBarColors? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    GhuslTopAppBar(
        modifier = modifier,
        titleContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        canNavigateBack = canNavigateBack,
        onBackClick = onBackClick,
        elevation = elevation,
        colors = colors,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhuslTopAppBar(
    modifier: Modifier = Modifier,
    titleContent: @Composable () -> Unit,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    elevation: Dp = 3.dp,
    colors: TopAppBarColors? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val defaultColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.primary,
        navigationIconContentColor = MaterialTheme.colorScheme.primary,
        actionIconContentColor = MaterialTheme.colorScheme.primary
    )
    val finalColors = colors ?: defaultColors

    Surface(
        modifier = modifier,
        shadowElevation = elevation,
        color = MaterialTheme.colorScheme.surface
    ) {
        TopAppBar(
            title = titleContent,
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_back)
                        )
                    }
                }
            },
            actions = actions,
            colors = finalColors
        )
    }
}
