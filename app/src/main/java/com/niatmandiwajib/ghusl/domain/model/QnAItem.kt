package com.niatmandiwajib.ghusl.domain.model

data class QnAItem(
    val id: Long,
    val question: String,
    val answer: String?,
    val status: QnAStatus,
    val createdAt: Long,
    val answeredAt: Long?
)

enum class QnAStatus {
    PENDING, PROCESSING, ANSWERED, ERROR
}
