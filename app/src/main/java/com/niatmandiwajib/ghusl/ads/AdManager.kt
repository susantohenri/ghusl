package com.niatmandiwajib.ghusl.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.niatmandiwajib.ghusl.data.remote.dto.AdConfigDto

class AdManager(private val context: Context) {

    companion object {
        private const val TAG = "AdManager"
        private const val INTERSTITIAL_COOLDOWN_MS = 3 * 60 * 1000L // 3 minutes
        private const val APP_OPEN_COOLDOWN_MS = 5 * 60 * 1000L // 5 minutes
    }

    private var consentInformation: ConsentInformation? = null
    private var interstitialAd: InterstitialAd? = null
    private var appOpenAd: AppOpenAd? = null
    private var rewardedAd: RewardedAd? = null
    private var lastInterstitialTime = 0L
    private var lastAppOpenTime = 0L
    private var adConfig: AdConfigDto? = null
    private var isFirstLaunch = true

    fun initialize(config: AdConfigDto) {
        adConfig = config
        try {
            MobileAds.initialize(context) {
                Log.d(TAG, "AdMob initialized")
                preloadInterstitial()
                preloadAppOpenAd()
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob init error", e)
        }
    }

    // --- UMP Consent ---
    fun requestConsent(activity: Activity, onConsentResult: (Boolean) -> Unit) {
        try {
            val params = ConsentRequestParameters.Builder().build()
            consentInformation = UserMessagingPlatform.getConsentInformation(context)
            consentInformation?.requestConsentInfoUpdate(
                activity, params,
                {
                    if (consentInformation?.isConsentFormAvailable == true) {
                        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                            onConsentResult(consentInformation?.canRequestAds() == true)
                        }
                    } else {
                        onConsentResult(consentInformation?.canRequestAds() == true)
                    }
                },
                { error -> 
                    Log.e(TAG, "Consent error: ${error.message}")
                    onConsentResult(true) // Fallback: allow ads
                }
            )
        } catch (e: Exception) {
            onConsentResult(true)
        }
    }

    // --- Interstitial (frequency capped) ---
    fun preloadInterstitial() {
        try {
            val unitId = adConfig?.interstitialAdUnitId ?: return
            InterstitialAd.load(context, unitId, AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad }
                    override fun onAdFailedToLoad(error: LoadAdError) { interstitialAd = null }
                })
        } catch (e: Exception) {
            Log.e(TAG, "Preload interstitial error", e)
        }
    }

    fun showInterstitialIfReady(activity: Activity, isFromWizard: Boolean = false): Boolean {
        if (isFromWizard) return false
        val now = System.currentTimeMillis()
        if (now - lastInterstitialTime < INTERSTITIAL_COOLDOWN_MS) return false
        try {
            interstitialAd?.let { ad ->
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        preloadInterstitial()
                    }
                }
                ad.show(activity)
                lastInterstitialTime = now
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Show interstitial error", e)
        }
        return false
    }

    // --- App Open Ad (with cooldown, not on first launch) ---
    fun preloadAppOpenAd() {
        try {
            val unitId = adConfig?.appOpenAdUnitId ?: return
            AppOpenAd.load(context, unitId, AdRequest.Builder().build(),
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) { appOpenAd = ad }
                    override fun onAdFailedToLoad(error: LoadAdError) { appOpenAd = null }
                })
        } catch (e: Exception) {
            Log.e(TAG, "Preload app open ad error", e)
        }
    }

    fun showAppOpenAdIfReady(activity: Activity): Boolean {
        if (isFirstLaunch) { isFirstLaunch = false; return false }
        val now = System.currentTimeMillis()
        if (now - lastAppOpenTime < APP_OPEN_COOLDOWN_MS) return false
        try {
            appOpenAd?.let { ad ->
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        preloadAppOpenAd()
                    }
                }
                ad.show(activity)
                lastAppOpenTime = now
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Show app open ad error", e)
        }
        return false
    }

    // --- Rewarded Ad ---
    fun preloadRewarded() {
        try {
            val unitId = adConfig?.rewardedAdUnitId ?: return
            RewardedAd.load(context, unitId, AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
                    override fun onAdFailedToLoad(error: LoadAdError) { rewardedAd = null }
                })
        } catch (e: Exception) {
            Log.e(TAG, "Preload rewarded error", e)
        }
    }

    fun showRewardedAd(activity: Activity, onRewarded: () -> Unit): Boolean {
        try {
            rewardedAd?.let { ad ->
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        rewardedAd = null
                        preloadRewarded()
                    }
                }
                ad.show(activity) { onRewarded() }
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Show rewarded error", e)
        }
        return false
    }

    fun getBannerUnitId(): String = adConfig?.bannerAdUnitId ?: "ca-app-pub-3940256099942544/6300978111"
    fun getNativeUnitId(): String = adConfig?.nativeAdUnitId ?: "ca-app-pub-3940256099942544/2247696110"
}
