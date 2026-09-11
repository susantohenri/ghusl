package com.niatmandiwajib.ghusl.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector? = null
) {
    data object Home : Screen(
        route = "home",
        titleResId = com.niatmandiwajib.ghusl.R.string.nav_home,
        icon = Icons.Filled.Home
    )
    data object Guide : Screen(
        route = "guide",
        titleResId = com.niatmandiwajib.ghusl.R.string.nav_guide,
        icon = Icons.Filled.MenuBook
    )
    data object Wizard : Screen(
        route = "wizard",
        titleResId = com.niatmandiwajib.ghusl.R.string.nav_wizard,
        icon = Icons.Filled.CheckCircle
    )
    data object AskUstadz : Screen(
        route = "ask_ustadz",
        titleResId = com.niatmandiwajib.ghusl.R.string.nav_ask_ustadz,
        icon = Icons.Filled.QuestionAnswer
    )
    data object More : Screen(
        route = "more",
        titleResId = com.niatmandiwajib.ghusl.R.string.nav_more,
        icon = Icons.Filled.MoreHoriz
    )

    // Sub-screens (no bottom nav icon)
    data object SlideShow : Screen(route = "slide_show/{contentId}", titleResId = com.niatmandiwajib.ghusl.R.string.nav_guide) {
        fun createRoute(contentId: String) = "slide_show/$contentId"
    }
    data object WizardResult : Screen(route = "wizard_result", titleResId = com.niatmandiwajib.ghusl.R.string.nav_wizard)
    data object QnADetail : Screen(route = "qna_detail/{questionId}", titleResId = com.niatmandiwajib.ghusl.R.string.nav_ask_ustadz) {
        fun createRoute(questionId: String) = "qna_detail/$questionId"
    }
    data object Settings : Screen(route = "settings", titleResId = com.niatmandiwajib.ghusl.R.string.nav_more)
    data object Search : Screen(route = "search", titleResId = com.niatmandiwajib.ghusl.R.string.nav_more)
    data object Bookmarks : Screen(route = "bookmarks", titleResId = com.niatmandiwajib.ghusl.R.string.nav_more)
    data object History : Screen(route = "history", titleResId = com.niatmandiwajib.ghusl.R.string.nav_more)
    data object About : Screen(route = "about", titleResId = com.niatmandiwajib.ghusl.R.string.nav_more)

    companion object {
        val bottomNavItems = listOf(Home, Guide, Wizard, AskUstadz, More)
    }
}
