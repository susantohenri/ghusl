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
                appOpenAdUnitId = "ca-app-pub-1012224604632017/2016194717",
                bannerAdUnitId = "ca-app-pub-1012224604632017/5653196554",
                interstitialAdUnitId = "ca-app-pub-1012224604632017/5820717514",
                nativeAdUnitId = "ca-app-pub-1012224604632017/3701820639",
                rewardedAdUnitId = "ca-app-pub-1012224604632017/7143117057"
            ).also { cachedConfig = it }
        }
    }
}
