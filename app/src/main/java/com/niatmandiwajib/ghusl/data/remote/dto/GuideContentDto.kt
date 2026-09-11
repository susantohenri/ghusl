package com.niatmandiwajib.ghusl.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import com.niatmandiwajib.ghusl.domain.model.Slide

data class GuideContentDto(
    val kode: String,
    val id: LocalizedContentDto?,
    val en: LocalizedContentDto?,
    val ms: LocalizedContentDto?,
    val ur: LocalizedContentDto?
) {
    fun getLocalized(language: String): LocalizedContentDto? {
        return when (language) {
            "id", "in" -> id
            "en" -> en
            "ms" -> ms
            "ur" -> ur
            else -> en ?: id
        }
    }

    fun toDomain(language: String, mediaBaseUrl: String): GuideContent? {
        val localized = getLocalized(language) ?: return null
        return GuideContent(
            kode = kode,
            title = localized.judulKonten,
            description = localized.deskripsiKonten,
            slides = localized.slideShow.filter { it.kodeSlide.isNotBlank() }.map { slideDto ->
                Slide(
                    kode = slideDto.kodeSlide,
                    title = slideDto.judulSlide,
                    imageUrl = if (slideDto.gambar.isNotBlank()) mediaBaseUrl + slideDto.gambar else "",
                    text = slideDto.text,
                    arabicText = slideDto.arab,
                    latinText = slideDto.latin,
                    translation = slideDto.terjemah,
                    source = slideDto.sumber,
                    audioUrl = if (slideDto.suara.isNotBlank()) mediaBaseUrl + slideDto.suara else ""
                )
            }
        )
    }
}

data class LocalizedContentDto(
    @SerializedName("judul_konten") val judulKonten: String,
    @SerializedName("deskripsi_konten") val deskripsiKonten: String,
    @SerializedName("slide_show") val slideShow: List<SlideDto>
)

data class SlideDto(
    @SerializedName("kode_slide") val kodeSlide: String = "",
    @SerializedName("judul_slide") val judulSlide: String = "",
    val gambar: String = "",
    val text: String = "",
    val arab: String = "",
    val latin: String = "",
    val terjemah: String = "",
    val sumber: String = "",
    val suara: String = ""
)
