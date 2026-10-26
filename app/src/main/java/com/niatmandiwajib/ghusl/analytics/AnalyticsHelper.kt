package com.niatmandiwajib.ghusl.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsHelper {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        } catch (e: Exception) {
            // Analytics init failed
        }
    }

    fun logEvent(name: String, params: (Bundle.() -> Unit)? = null) {
        try {
            val bundle = params?.let { Bundle().apply(it) }
            firebaseAnalytics?.logEvent(name, bundle)
        } catch (e: Exception) {
            // Log event failed silently
        }
    }
}
