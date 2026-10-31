package com.niatmandiwajib.ghusl.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.niatmandiwajib.ghusl.BuildConfig
import com.niatmandiwajib.ghusl.ui.navigation.Screen

/**
 * Singleton wrapper and canonical entry point for Firebase Analytics instrumentation.
 * Ensures safe null handling, application context usage (leak prevention),
 * anti-duplicate screen tracking, and failure resilience.
 */
object AnalyticsLogger {

    private const val TAG = "AnalyticsLogger"

    @Volatile
    private var firebaseAnalytics: FirebaseAnalytics? = null

    @Volatile
    private var lastTrackedScreen: String? = null

    /**
     * Initializes Firebase Analytics with Application Context,
     * enables analytics collection explicitly, and sets default event parameters.
     */
    fun init(context: Context) {
        try {
            val appContext = context.applicationContext
            val analytics = FirebaseAnalytics.getInstance(appContext)
            analytics.setAnalyticsCollectionEnabled(true)

            // Default event parameter: app_version
            val defaultParams = Bundle().apply {
                putString("app_version", BuildConfig.VERSION_NAME)
            }
            analytics.setDefaultEventParameters(defaultParams)

            firebaseAnalytics = analytics
            Log.d(TAG, "Firebase Analytics initialized successfully with app_version=${BuildConfig.VERSION_NAME}")

            // Initialize Firebase Crashlytics collection explicitly
            try {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
                Log.d(TAG, "Firebase Crashlytics initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Firebase Crashlytics", e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Analytics", e)
        }
    }

    /**
     * Logs a custom event with optional parameters bundle.
     */
    fun logEvent(name: String, params: Bundle? = null) {
        if (name.isBlank()) return
        try {
            firebaseAnalytics?.logEvent(name, params)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log event '$name'", e)
        }
    }

    /**
     * Convenience builder to log a custom event with a lambda block.
     */
    fun logEvent(name: String, block: Bundle.() -> Unit) {
        if (name.isBlank()) return
        try {
            val bundle = Bundle().apply(block)
            firebaseAnalytics?.logEvent(name, bundle)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log event '$name'", e)
        }
    }

    /**
     * Logs screen_view event using standard Firebase parameters.
     * Implements anti-duplication to prevent recording identical consecutive screens.
     */
    fun logScreenView(screenName: String, screenClass: String? = null) {
        if (screenName.isBlank()) return
        if (screenName == lastTrackedScreen) return

        lastTrackedScreen = screenName
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass ?: screenName)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            Log.d(TAG, "Screen view tracked: screen_name=$screenName, screen_class=${screenClass ?: screenName}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log screen_view for '$screenName'", e)
        }
    }

    /**
     * Resolves Jetpack Compose navigation routes into canonical lowercase snake_case
     * screen names and corresponding screen classes, then logs screen_view.
     */
    fun logScreenFromRoute(route: String?) {
        if (route.isNullOrBlank()) return
        val (screenName, screenClass) = when {
            route == Screen.Home.route -> "main" to "HomeScreen"
            route == Screen.Guide.route -> "guide" to "GuideListScreen"
            route == Screen.Wizard.route -> "wizard" to "WizardScreen"
            route == Screen.AskUstadz.route -> "ask_ustadz" to "AskUstadzScreen"
            route == Screen.More.route -> "more" to "MoreScreen"
            route.startsWith("slide_show") -> "slide_show" to "SlideShowScreen"
            route == Screen.WizardResult.route -> "wizard_result" to "WizardResultScreen"
            route.startsWith("qna_detail") -> "qna_detail" to "QnADetailScreen"
            route == Screen.Settings.route -> "settings" to "SettingsScreen"
            route == Screen.Search.route -> "search" to "SearchScreen"
            route == Screen.Bookmarks.route -> "bookmarks" to "BookmarkScreen"
            route == Screen.History.route -> "history" to "HistoryScreen"
            route == Screen.About.route -> "about" to "AboutScreen"
            route == Screen.Faq.route -> "faq" to "FaqScreen"
            else -> {
                val clean = route.substringBefore('/').substringBefore('?').lowercase()
                clean to clean
            }
        }
        logScreenView(screenName, screenClass)
    }

    /**
     * Sets a user property safely.
     */
    fun setUserProperty(name: String, value: String?) {
        if (name.isBlank()) return
        try {
            firebaseAnalytics?.setUserProperty(name, value)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user property '$name'", e)
        }
    }

    /**
     * Records a non-fatal exception to Firebase Crashlytics safely.
     */
    fun recordException(throwable: Throwable) {
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record exception in Crashlytics", e)
        }
    }

    /**
     * Writes a custom log line to the next Firebase Crashlytics report.
     */
    fun logCrashlytics(message: String) {
        if (message.isBlank()) return
        try {
            FirebaseCrashlytics.getInstance().log(message)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write Crashlytics log", e)
        }
    }

    /**
     * Sets a custom key-value pair for Crashlytics diagnostic context.
     */
    fun setCrashlyticsCustomKey(key: String, value: String) {
        if (key.isBlank()) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set Crashlytics custom key '$key'", e)
        }
    }
}
