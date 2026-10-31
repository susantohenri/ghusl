package com.niatmandiwajib.ghusl.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.niatmandiwajib.ghusl.BuildConfig
import com.niatmandiwajib.ghusl.ui.navigation.Screen
import java.util.Locale

/**
 * Singleton wrapper and canonical entry point for Firebase Analytics instrumentation.
 * Ensures safe null handling, application context usage (leak prevention),
 * anti-duplicate screen tracking, and failure resilience.
 */
object AnalyticsLogger {

    private const val TAG = "AnalyticsLogger"
    private const val PREFS_NAME = "analytics_user_properties"
    private const val KEY_FIRST_SEEN = "first_seen_timestamp"
    private const val KEY_LAST_SEEN = "last_seen_timestamp"
    private const val KEY_CACHED_LANGUAGE = "cached_language"
    private const val KEY_CACHED_COUNTRY = "cached_country"
    private const val KEY_CACHED_ENGAGEMENT = "cached_engagement_level"

    private const val ONE_DAY_MS = 24 * 60 * 60 * 1000L
    private const val SEVEN_DAYS_MS = 7 * ONE_DAY_MS

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

            // Initialize user properties (language, country, engagement_level)
            updateUserProperties(appContext)

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
     * Evaluates and updates user properties (language, country, engagement_level).
     * Caches values in SharedPreferences to avoid redundant setUserProperty calls.
     */
    fun updateUserProperties(context: Context) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val now = System.currentTimeMillis()

            val firstSeen = prefs.getLong(KEY_FIRST_SEEN, 0L)
            val lastSeen = prefs.getLong(KEY_LAST_SEEN, 0L)

            val actualFirstSeen = if (firstSeen == 0L) {
                now
            } else {
                firstSeen
            }

            // Evaluate engagement_level based on prior activity before updating lastSeen
            val engagementLevel = when {
                (now - actualFirstSeen) <= ONE_DAY_MS -> "new"
                lastSeen > 0L && (now - lastSeen) <= SEVEN_DAYS_MS -> "active"
                lastSeen > 0L && (now - lastSeen) > SEVEN_DAYS_MS -> "dormant"
                else -> "new"
            }

            val locale = Locale.getDefault()
            val language = locale.language.takeIf { it.isNotBlank() } ?: "unknown"
            val country = locale.country.takeIf { it.isNotBlank() } ?: "unknown"

            val cachedLanguage = prefs.getString(KEY_CACHED_LANGUAGE, null)
            val cachedCountry = prefs.getString(KEY_CACHED_COUNTRY, null)
            val cachedEngagement = prefs.getString(KEY_CACHED_ENGAGEMENT, null)

            val editor = prefs.edit()
            if (firstSeen == 0L) {
                editor.putLong(KEY_FIRST_SEEN, actualFirstSeen)
            }
            editor.putLong(KEY_LAST_SEEN, now)

            if (cachedLanguage != language) {
                setUserProperty("language", language)
                editor.putString(KEY_CACHED_LANGUAGE, language)
                Log.d(TAG, "User property set: language=$language")
            }

            if (cachedCountry != country) {
                setUserProperty("country", country)
                editor.putString(KEY_CACHED_COUNTRY, country)
                Log.d(TAG, "User property set: country=$country")
            }

            if (cachedEngagement != engagementLevel) {
                setUserProperty("engagement_level", engagementLevel)
                editor.putString(KEY_CACHED_ENGAGEMENT, engagementLevel)
                Log.d(TAG, "User property set: engagement_level=$engagementLevel")
            }

            editor.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user properties", e)
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
     * Current canonical screen name tracked by the app.
     */
    val currentScreen: String
        get() = lastTrackedScreen ?: "main"

    /**
     * Logs ad_request right before loadAd is invoked.
     */
    fun logAdRequest(adUnitId: String, adFormat: String, screen: String = currentScreen) {
        if (adUnitId.isBlank()) return
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            putString("screen", screen)
        }
        logEvent("ad_request", bundle)
        Log.d(TAG, "ad_request: format=$adFormat, unit=$adUnitId, screen=$screen")
    }

    /**
     * Logs ad_impression via OnPaidEventListener with accurate revenue calculation.
     * Prioritizes OnPaidEventListener as the single canonical impression source.
     */
    fun logAdImpression(
        adUnitId: String,
        adFormat: String,
        adSource: String?,
        currency: String?,
        valueMicros: Long,
        screen: String = currentScreen
    ) {
        if (adUnitId.isBlank()) return
        val value = valueMicros / 1_000_000.0
        val curr = if (currency.isNullOrBlank()) "USD" else currency

        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            if (!adSource.isNullOrBlank()) {
                putString("ad_source", adSource)
                putString(FirebaseAnalytics.Param.AD_SOURCE, adSource)
            }
            putString("screen", screen)
            putString("currency", curr)
            putDouble("value", value)

            // Official Firebase Analytics constants
            putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId)
            putString(FirebaseAnalytics.Param.AD_FORMAT, adFormat)
            putString(FirebaseAnalytics.Param.CURRENCY, curr)
            putDouble(FirebaseAnalytics.Param.VALUE, value)
        }
        logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle)
        Log.d(TAG, "ad_impression: format=$adFormat, unit=$adUnitId, value=$value $curr, source=$adSource, screen=$screen")
    }

    /**
     * Logs ad_click when an ad is clicked.
     */
    fun logAdClick(adUnitId: String, adFormat: String, screen: String = currentScreen) {
        if (adUnitId.isBlank()) return
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            putString("screen", screen)
        }
        logEvent("ad_click", bundle)
        Log.d(TAG, "ad_click: format=$adFormat, unit=$adUnitId, screen=$screen")
    }

    /**
     * Logs ad_failed when an ad fails to load.
     */
    fun logAdFailed(adUnitId: String, adFormat: String, errorCode: Int, screen: String = currentScreen) {
        if (adUnitId.isBlank()) return
        val bundle = Bundle().apply {
            putString("ad_unit_id", adUnitId)
            putString("ad_format", adFormat)
            putInt("error_code", errorCode)
            putString("screen", screen)
        }
        logEvent("ad_failed", bundle)
        Log.d(TAG, "ad_failed: format=$adFormat, unit=$adUnitId, error_code=$errorCode, screen=$screen")
    }

    /**
     * Logs notification_opened when the user opens a notification.
     */
    fun logNotificationOpened(notificationType: String) {
        if (notificationType.isBlank()) return
        val bundle = Bundle().apply {
            putString("notification_type", notificationType)
        }
        logEvent("notification_opened", bundle)
        Log.d(TAG, "notification_opened: type=$notificationType")
    }

    /**
     * Logs wizard_completed when user finishes the decision tree condition check.
     */
    fun logWizardCompleted(resultId: String, mustGhusl: Boolean, stepCount: Int) {
        val bundle = Bundle().apply {
            putString("result_id", resultId)
            putString("must_ghusl", mustGhusl.toString())
            putInt("step_count", stepCount)
        }
        logEvent("wizard_completed", bundle)
        Log.d(TAG, "wizard_completed: result=$resultId, must_ghusl=$mustGhusl, steps=$stepCount")
    }

    /**
     * Logs guide_completed when user navigates to the final slide of a guide.
     */
    fun logGuideCompleted(guideId: String, totalSlides: Int) {
        if (guideId.isBlank()) return
        val bundle = Bundle().apply {
            putString("guide_id", guideId)
            putInt("total_slides", totalSlides)
        }
        logEvent("guide_completed", bundle)
        Log.d(TAG, "guide_completed: guide_id=$guideId, total_slides=$totalSlides")
    }

    /**
     * Logs question_submitted when a user submits a question to Tanya Ustadz AI.
     */
    fun logQuestionSubmitted(questionId: String, isFirstQuestion: Boolean) {
        if (questionId.isBlank()) return
        val bundle = Bundle().apply {
            putString("question_id", questionId)
            putString("is_first_question", isFirstQuestion.toString())
        }
        logEvent("question_submitted", bundle)
        Log.d(TAG, "question_submitted: id=$questionId, is_first=$isFirstQuestion")
    }

    /**
     * Logs niat_copied when user copies intention text to clipboard.
     */
    fun logNiatCopied(niatType: String) {
        if (niatType.isBlank()) return
        val bundle = Bundle().apply {
            putString("niat_type", niatType)
        }
        logEvent("niat_copied", bundle)
        Log.d(TAG, "niat_copied: type=$niatType")
    }

    /**
     * Logs niat_shared when user shares intention text.
     */
    fun logNiatShared(niatType: String) {
        if (niatType.isBlank()) return
        val bundle = Bundle().apply {
            putString("niat_type", niatType)
        }
        logEvent("niat_shared", bundle)
        Log.d(TAG, "niat_shared: type=$niatType")
    }

    /**
     * Logs standard Firebase search event when user searches guides.
     */
    fun logSearch(searchTerm: String) {
        if (searchTerm.isBlank()) return
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm)
        }
        logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        Log.d(TAG, "search: term=$searchTerm")
    }

    /**
     * Logs guide_bookmarked when user bookmarks a guide.
     */
    fun logGuideBookmarked(guideId: String) {
        if (guideId.isBlank()) return
        val bundle = Bundle().apply {
            putString("guide_id", guideId)
        }
        logEvent("guide_bookmarked", bundle)
        Log.d(TAG, "guide_bookmarked: guide_id=$guideId")
    }
}
