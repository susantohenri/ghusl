package com.niatmandiwajib.ghusl.domain.model

data class GuideContent(
    val kode: String,
    val title: String,
    val description: String,
    val slides: List<Slide>
)

data class Slide(
    val kode: String,
    val title: String,
    val imageUrl: String,
    val text: String,
    val arabicText: String,
    val latinText: String,
    val translation: String,
    val source: String,
    val audioUrl: String
)
