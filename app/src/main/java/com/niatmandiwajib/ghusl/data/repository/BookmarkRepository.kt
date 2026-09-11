package com.niatmandiwajib.ghusl.data.repository

import com.niatmandiwajib.ghusl.data.local.dao.BookmarkDao
import com.niatmandiwajib.ghusl.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarked(contentKode: String): Flow<Boolean> = bookmarkDao.isBookmarked(contentKode)

    suspend fun addBookmark(contentKode: String, title: String, slideKode: String? = null) {
        bookmarkDao.insertBookmark(
            BookmarkEntity(
                contentKode = contentKode,
                slideKode = slideKode,
                title = title
            )
        )
    }

    suspend fun removeBookmark(contentKode: String) {
        bookmarkDao.deleteBookmark(contentKode)
    }
}
