package com.niatmandiwajib.ghusl.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niatmandiwajib.ghusl.data.local.entity.GuideContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideDao {
    @Query("SELECT * FROM guide_contents")
    fun getAllGuides(): Flow<List<GuideContentEntity>>

    @Query("SELECT * FROM guide_contents")
    suspend fun getAllGuidesSync(): List<GuideContentEntity>

    @Query("SELECT * FROM guide_contents WHERE kode = :kode LIMIT 1")
    suspend fun getGuideByKode(kode: String): GuideContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuides(guides: List<GuideContentEntity>)

    @Query("SELECT COUNT(*) FROM guide_contents")
    suspend fun getGuideCount(): Int

    @Query("DELETE FROM guide_contents")
    suspend fun clearAll()
}
