package com.niatmandiwajib.ghusl.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read_history")
data class ReadHistoryEntity(
    @PrimaryKey val contentKode: String,
    val lastSlideIndex: Int = 0,
    val title: String,
    val lastReadAt: Long = System.currentTimeMillis()
)
