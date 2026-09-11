package com.niatmandiwajib.ghusl.data.repository

import com.niatmandiwajib.ghusl.data.local.dao.ReadHistoryDao
import com.niatmandiwajib.ghusl.data.local.entity.ReadHistoryEntity
import kotlinx.coroutines.flow.Flow

class ReadHistoryRepository(private val readHistoryDao: ReadHistoryDao) {

    fun getAllHistory(): Flow<List<ReadHistoryEntity>> = readHistoryDao.getAllHistory()

    fun getLastRead(): Flow<ReadHistoryEntity?> = readHistoryDao.getLastRead()

    suspend fun updateHistory(contentKode: String, slideIndex: Int, title: String) {
        readHistoryDao.upsertHistory(
            ReadHistoryEntity(
                contentKode = contentKode,
                lastSlideIndex = slideIndex,
                title = title
            )
        )
    }

    suspend fun clearHistory() = readHistoryDao.clearHistory()
}
