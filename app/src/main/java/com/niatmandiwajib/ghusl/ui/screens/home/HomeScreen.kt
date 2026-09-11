package com.niatmandiwajib.ghusl.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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

        Spacer(modifier = Modifier.height(16.dp))

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
}
