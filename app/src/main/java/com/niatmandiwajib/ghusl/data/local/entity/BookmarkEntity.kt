package com.niatmandiwajib.ghusl.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val contentKode: String,
    val slideKode: String? = null,
    val title: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
