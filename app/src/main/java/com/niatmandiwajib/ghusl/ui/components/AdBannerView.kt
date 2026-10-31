package com.niatmandiwajib.ghusl.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.niatmandiwajib.ghusl.analytics.AnalyticsLogger

@Composable
fun AdBannerView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, getScreenWidthDp(ctx)))
                this.adUnitId = adUnitId

                adListener = object : AdListener() {
                    override fun onAdClicked() {
                        AnalyticsLogger.logAdClick(adUnitId, "BANNER")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        AnalyticsLogger.logAdFailed(adUnitId, "BANNER", error.code)
                    }
                }

                setOnPaidEventListener { adValue ->
                    val adSource = responseInfo?.loadedAdapterResponseInfo?.adSourceName
                    AnalyticsLogger.logAdImpression(
                        adUnitId = adUnitId,
                        adFormat = "BANNER",
                        adSource = adSource,
                        currency = adValue.currencyCode,
                        valueMicros = adValue.valueMicros
                    )
                }

                AnalyticsLogger.logAdRequest(adUnitId, "BANNER")
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

private fun getScreenWidthDp(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return (displayMetrics.widthPixels / displayMetrics.density).toInt()
}
