package com.niatmandiwajib.ghusl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.niatmandiwajib.ghusl.ui.screens.guide.GuideListScreen
import com.niatmandiwajib.ghusl.ui.screens.home.HomeScreen
import com.niatmandiwajib.ghusl.ui.screens.more.MoreScreen
import com.niatmandiwajib.ghusl.ui.screens.ustadz.AskUstadzScreen
import com.niatmandiwajib.ghusl.ui.screens.wizard.WizardScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Guide.route) {
            GuideListScreen(navController = navController)
        }
        composable(Screen.Wizard.route) {
            WizardScreen(navController = navController)
        }
        composable(Screen.AskUstadz.route) {
            AskUstadzScreen(navController = navController)
        }
        composable(Screen.More.route) {
            MoreScreen(navController = navController)
        }
        // Sub-screens — will be implemented in later fases
        composable(
            Screen.SlideShow.route,
            arguments = listOf(navArgument("contentId") { type = NavType.StringType })
        ) { /* TODO: SlideShowScreen */ }
        composable(Screen.WizardResult.route) { /* TODO: WizardResultScreen */ }
        composable(
            Screen.QnADetail.route,
            arguments = listOf(navArgument("questionId") { type = NavType.StringType })
        ) { /* TODO: QnADetailScreen */ }
        composable(Screen.Settings.route) { /* TODO: SettingsScreen */ }
        composable(Screen.Search.route) { /* TODO: SearchScreen */ }
        composable(Screen.Bookmarks.route) { /* TODO: BookmarkScreen */ }
        composable(Screen.History.route) { /* TODO: HistoryScreen */ }
        composable(Screen.About.route) { /* TODO: AboutScreen */ }
    }
}
