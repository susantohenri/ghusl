package com.niatmandiwajib.ghusl.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guide_contents")
data class GuideContentEntity(
    @PrimaryKey val kode: String,
    val jsonPayload: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
