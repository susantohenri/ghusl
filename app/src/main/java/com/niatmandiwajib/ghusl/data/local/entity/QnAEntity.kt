package com.niatmandiwajib.ghusl.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qna_history")
data class QnAEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val answer: String? = null,
    val status: String = "PENDING", // PENDING, PROCESSING, ANSWERED, ERROR
    val createdAt: Long = System.currentTimeMillis(),
    val answeredAt: Long? = null
)
