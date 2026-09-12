package com.niatmandiwajib.ghusl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.niatmandiwajib.ghusl.ui.screens.guide.GuideListScreen
import com.niatmandiwajib.ghusl.ui.screens.guide.SlideShowScreen
import com.niatmandiwajib.ghusl.ui.screens.home.HomeScreen
import com.niatmandiwajib.ghusl.ui.screens.more.*
import com.niatmandiwajib.ghusl.ui.screens.ustadz.AskUstadzScreen
import com.niatmandiwajib.ghusl.ui.screens.ustadz.QnADetailScreen
import com.niatmandiwajib.ghusl.ui.screens.wizard.WizardResultScreen
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

        // Sub-screens
        composable(
            Screen.SlideShow.route,
            arguments = listOf(navArgument("contentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: return@composable
            SlideShowScreen(
                contentId = contentId,
                navController = navController
            )
        }

        composable(Screen.WizardResult.route) {
            WizardResultScreen(navController = navController)
        }

        composable(
            Screen.QnADetail.route,
            arguments = listOf(navArgument("questionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getString("questionId")?.toLongOrNull() ?: return@composable
            QnADetailScreen(
                questionId = questionId,
                navController = navController
            )
        }

        // More Screens
        composable(Screen.Settings.route) { SettingsScreen(navController = navController) }
        composable(Screen.Search.route) { SearchScreen(navController = navController) }
        composable(Screen.Bookmarks.route) { BookmarkScreen(navController = navController) }
        composable(Screen.History.route) { HistoryScreen(navController = navController) }
        composable(Screen.About.route) { AboutScreen(navController = navController) }
        composable(Screen.Faq.route) { FaqScreen(navController = navController) }
    }
}
