package com.niatmandiwajib.ghusl.data.local.dao

import androidx.room.*
import com.niatmandiwajib.ghusl.data.local.entity.QnAEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QnADao {
    @Query("SELECT * FROM qna_history ORDER BY createdAt DESC")
    fun getAllQnA(): Flow<List<QnAEntity>>

    @Query("SELECT * FROM qna_history WHERE id = :id")
    fun getQnAById(id: Long): Flow<QnAEntity?>

    @Query("SELECT * FROM qna_history WHERE status = 'PENDING' OR status = 'PROCESSING'")
    fun getPendingQnA(): Flow<List<QnAEntity>>

    @Query("SELECT COUNT(*) FROM qna_history")
    suspend fun getQuestionCount(): Int

    @Insert
    suspend fun insertQuestion(entity: QnAEntity): Long

    @Query("UPDATE qna_history SET answer = :answer, status = :status, answeredAt = :answeredAt WHERE id = :id")
    suspend fun updateAnswer(id: Long, answer: String, status: String, answeredAt: Long)

    @Query("UPDATE qna_history SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
