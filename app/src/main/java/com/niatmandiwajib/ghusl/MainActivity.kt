package com.niatmandiwajib.ghusl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.niatmandiwajib.ghusl.ui.navigation.BottomNavBar
import com.niatmandiwajib.ghusl.ui.navigation.NavGraph
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import com.niatmandiwajib.ghusl.ui.theme.GhuslTheme
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
        val app = application as GhuslApplication
        app.adManager.requestConsent(this) { canRequestAds ->
            // Ads will be loaded based on consent result
        }
        setContent {
            val themeMode by app.container.userPreferences.themeMode.collectAsState(initial = "system")
            GhuslTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Guide.route,
                    Screen.Wizard.route,
                    Screen.AskUstadz.route,
                    Screen.More.route
                )

                val showAd = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Guide.route,
                    Screen.AskUstadz.route,
                    Screen.Search.route,
                    Screen.Bookmarks.route,
                    Screen.History.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showAd || showBottomBar) {
                            androidx.compose.foundation.layout.Column(
                                modifier = if (!showBottomBar) {
                                    Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                                } else {
                                    Modifier
                                }
                            ) {
                                if (showAd) {
                                    com.niatmandiwajib.ghusl.ui.components.AdBannerView(adUnitId = app.adManager.getBannerUnitId())
                                }
                                if (showBottomBar) {
                                    BottomNavBar(navController = navController)
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
