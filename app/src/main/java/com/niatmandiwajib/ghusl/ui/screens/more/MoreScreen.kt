package com.niatmandiwajib.ghusl.ui.screens.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.nav_more)) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                MoreMenuItem(
                    icon = Icons.Default.Search,
                    title = stringResource(R.string.more_search),
                    onClick = { navController.navigate(Screen.Search.route) }
                )
                HorizontalDivider()
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.Bookmark,
                    title = stringResource(R.string.more_bookmarks),
                    onClick = { navController.navigate(Screen.Bookmarks.route) }
                )
                HorizontalDivider()
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.History,
                    title = stringResource(R.string.more_history),
                    onClick = { navController.navigate(Screen.History.route) }
                )
                HorizontalDivider()
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.Help,
                    title = stringResource(R.string.more_faq),
                    onClick = { navController.navigate(Screen.Faq.route) }
                )
                HorizontalDivider()
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.Settings,
                    title = stringResource(R.string.more_settings),
                    onClick = { navController.navigate(Screen.Settings.route) }
                )
                HorizontalDivider()
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.more_about),
                    onClick = { navController.navigate(Screen.About.route) }
                )
            }
        }
    }
}

@Composable
fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
