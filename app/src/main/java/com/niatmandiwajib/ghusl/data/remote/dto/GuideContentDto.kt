package com.niatmandiwajib.ghusl.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.niatmandiwajib.ghusl.domain.model.GuideContent
import com.niatmandiwajib.ghusl.domain.model.Slide

data class GuideContentDto(
    val kode: String = "",
    val translations: ContentTranslationsDto? = null,
    @SerializedName("slide_show") val slideShow: List<SlideDto>? = null,
    // Direct fields for backward compatibility if translations wrapper is not present
    val id: LocalizedContentDto? = null,
    val en: LocalizedContentDto? = null,
    val ms: LocalizedContentDto? = null,
    val ur: LocalizedContentDto? = null
) {
    fun toDomain(language: String, mediaBaseUrl: String): GuideContent? {
        val localizedHeader = translations?.getLocalized(language)
        val legacyHeader = when (language) {
            "id", "in" -> id
            "en" -> en
            "ms" -> ms
            "ur" -> ur
            else -> en ?: id
        }

        val title = localizedHeader?.judulKonten?.ifEmpty { null } ?: legacyHeader?.judulKonten ?: return null
        val description = localizedHeader?.deskripsiKonten?.ifEmpty { null } ?: legacyHeader?.deskripsiKonten ?: ""

        val rawSlides = slideShow ?: legacyHeader?.slideShow ?: emptyList()

        return GuideContent(
            kode = kode,
            title = title,
            description = description,
            slides = rawSlides.filter { it.kodeSlide.isNotBlank() }.map { slideDto ->
                slideDto.toDomain(language, mediaBaseUrl)
            }
        )
    }
}

data class ContentTranslationsDto(
    val id: LocalizedContentHeaderDto? = null,
    val en: LocalizedContentHeaderDto? = null,
    val ms: LocalizedContentHeaderDto? = null,
    val ur: LocalizedContentHeaderDto? = null
) {
    fun getLocalized(language: String): LocalizedContentHeaderDto? {
        return when (language) {
            "id", "in" -> id
            "en" -> en
            "ms" -> ms
            "ur" -> ur
            else -> en ?: id
        }
    }
}

data class LocalizedContentHeaderDto(
    @SerializedName("judul_konten") val judulKonten: String = "",
    @SerializedName("deskripsi_konten") val deskripsiKonten: String = ""
)

data class LocalizedContentDto(
    @SerializedName("judul_konten") val judulKonten: String = "",
    @SerializedName("deskripsi_konten") val deskripsiKonten: String = "",
    @SerializedName("slide_show") val slideShow: List<SlideDto> = emptyList()
)

data class SlideDto(
    @SerializedName("kode_slide") val kodeSlide: String = "",
    val gambar: String = "",
    val arab: String = "",
    val latin: String = "",
    val suara: String = "",
    val translations: SlideTranslationsDto? = null,
    // Direct fields for backward compatibility
    @SerializedName("judul_slide") val judulSlide: String = "",
    val text: String = "",
    val terjemah: String = "",
    val sumber: String = ""
) {
    fun toDomain(language: String, mediaBaseUrl: String): Slide {
        val localizedSlide = translations?.getLocalized(language)

        val title = localizedSlide?.judulSlide?.ifEmpty { null } ?: judulSlide
        val slideText = localizedSlide?.text?.ifEmpty { null } ?: text
        val translationText = localizedSlide?.terjemah?.ifEmpty { null } ?: terjemah
        val sourceText = localizedSlide?.sumber?.ifEmpty { null } ?: sumber

        return Slide(
            kode = kodeSlide,
            title = title,
            imageUrl = if (gambar.isNotBlank()) mediaBaseUrl + gambar else "",
            text = slideText,
            arabicText = arab,
            latinText = latin,
            translation = translationText,
            source = sourceText,
            audioUrl = if (suara.isNotBlank()) mediaBaseUrl + suara else ""
        )
    }
}

data class SlideTranslationsDto(
    val id: LocalizedSlideDto? = null,
    val en: LocalizedSlideDto? = null,
    val ms: LocalizedSlideDto? = null,
    val ur: LocalizedSlideDto? = null
) {
    fun getLocalized(language: String): LocalizedSlideDto? {
        return when (language) {
            "id", "in" -> id
            "en" -> en
            "ms" -> ms
            "ur" -> ur
            else -> en ?: id
        }
    }
}

data class LocalizedSlideDto(
    @SerializedName("judul_slide") val judulSlide: String = "",
    val text: String = "",
    val terjemah: String = "",
    val sumber: String = ""
)
