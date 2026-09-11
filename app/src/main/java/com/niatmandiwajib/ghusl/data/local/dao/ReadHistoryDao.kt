package com.niatmandiwajib.ghusl.data.local.dao

import androidx.room.*
import com.niatmandiwajib.ghusl.data.local.entity.ReadHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadHistoryDao {
    @Query("SELECT * FROM read_history ORDER BY lastReadAt DESC")
    fun getAllHistory(): Flow<List<ReadHistoryEntity>>

    @Query("SELECT * FROM read_history ORDER BY lastReadAt DESC LIMIT 1")
    fun getLastRead(): Flow<ReadHistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHistory(history: ReadHistoryEntity)

    @Query("DELETE FROM read_history")
    suspend fun clearHistory()
}
