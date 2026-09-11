package com.niatmandiwajib.ghusl.data.local.dao

import androidx.room.*
import com.niatmandiwajib.ghusl.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE contentKode = :contentKode")
    suspend fun deleteBookmark(contentKode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE contentKode = :contentKode)")
    fun isBookmarked(contentKode: String): Flow<Boolean>
}
