package com.niatmandiwajib.ghusl.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AdConfigDto(
    @SerializedName("appOpenAdUnitId") val appOpenAdUnitId: String = "",
    @SerializedName("bannerAdUnitId") val bannerAdUnitId: String = "",
    @SerializedName("interstitialAdUnitId") val interstitialAdUnitId: String = "",
    @SerializedName("nativeAdUnitId") val nativeAdUnitId: String = "",
    @SerializedName("rewardedAdUnitId") val rewardedAdUnitId: String = ""
)
