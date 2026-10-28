package com.niatmandiwajib.ghusl.data.repository

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.niatmandiwajib.ghusl.R
import com.niatmandiwajib.ghusl.data.remote.api.GuideApiService
import com.niatmandiwajib.ghusl.data.remote.dto.AdConfigDto
import kotlinx.coroutines.tasks.await

class AdRepository(private val apiService: GuideApiService) {
    private var cachedConfig: AdConfigDto? = null

    suspend fun getAdConfig(): AdConfigDto {
        cachedConfig?.let { return it }

        return try {
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

            try {
                remoteConfig.fetchAndActivate().await()
            } catch (e: Exception) {
                // Ignore fetch failure, fallback to defaults
            }

            val appOpen = remoteConfig.getString("appOpenAdUnitId")
            val banner = remoteConfig.getString("bannerAdUnitId")
            val interstitial = remoteConfig.getString("interstitialAdUnitId")
            val native = remoteConfig.getString("nativeAdUnitId")
            val rewarded = remoteConfig.getString("rewardedAdUnitId")

            if (banner.isNotBlank()) {
                AdConfigDto(
                    appOpenAdUnitId = appOpen,
                    bannerAdUnitId = banner,
                    interstitialAdUnitId = interstitial,
                    nativeAdUnitId = native,
                    rewardedAdUnitId = rewarded
                ).also { cachedConfig = it }
            } else {
                fetchFromApiFallback()
            }
        } catch (e: Exception) {
            fetchFromApiFallback()
        }
    }

    private suspend fun fetchFromApiFallback(): AdConfigDto {
        return try {
            apiService.getAdConfig().also { cachedConfig = it }
        } catch (e: Exception) {
            AdConfigDto(
                appOpenAdUnitId = "ca-app-pub-3360913653749022/7868657747",
                bannerAdUnitId = "ca-app-pub-3360913653749022/9177121558",
                interstitialAdUnitId = "ca-app-pub-3360913653749022/7161954421",
                nativeAdUnitId = "ca-app-pub-3360913653749022/6694428444",
                rewardedAdUnitId = "ca-app-pub-3360913653749022/7864039881"
            ).also { cachedConfig = it }
        }
    }
}
