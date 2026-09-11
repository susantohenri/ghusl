package com.niatmandiwajib.ghusl.data.repository

import com.niatmandiwajib.ghusl.data.remote.api.GuideApiService
import com.niatmandiwajib.ghusl.data.remote.dto.AdConfigDto

class AdRepository(private val apiService: GuideApiService) {
    private var cachedConfig: AdConfigDto? = null

    suspend fun getAdConfig(): AdConfigDto {
        return cachedConfig ?: try {
            apiService.getAdConfig().also { cachedConfig = it }
        } catch (e: Exception) {
            // Fallback to test ad unit IDs
            AdConfigDto(
                appOpenAdUnitId = "ca-app-pub-3940256099942544/9257395921",
                bannerAdUnitId = "ca-app-pub-3940256099942544/6300978111",
                interstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712",
                nativeAdUnitId = "ca-app-pub-3940256099942544/2247696110",
                rewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917"
            )
        }
    }
}
