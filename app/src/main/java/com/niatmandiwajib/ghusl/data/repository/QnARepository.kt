package com.niatmandiwajib.ghusl.data.repository

import com.niatmandiwajib.ghusl.data.local.dao.QnADao
import com.niatmandiwajib.ghusl.data.local.entity.QnAEntity
import com.niatmandiwajib.ghusl.domain.model.QnAItem
import com.niatmandiwajib.ghusl.domain.model.QnAStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QnARepository(private val qnADao: QnADao) {

    fun getAllQnA(): Flow<List<QnAItem>> = qnADao.getAllQnA().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getQnAById(id: Long): Flow<QnAItem?> = qnADao.getQnAById(id).map { it?.toDomain() }

    suspend fun submitQuestion(question: String): Long {
        return qnADao.insertQuestion(
            QnAEntity(question = question, status = "PENDING")
        )
    }

    suspend fun updateAnswer(id: Long, answer: String) {
        qnADao.updateAnswer(
            id = id,
            answer = answer,
            status = "ANSWERED",
            answeredAt = System.currentTimeMillis()
        )
    }

    suspend fun updateStatus(id: Long, status: QnAStatus) {
        qnADao.updateStatus(id, status.name)
    }

    private fun QnAEntity.toDomain(): QnAItem {
        return QnAItem(
            id = id,
            question = question,
            answer = answer,
            status = try { QnAStatus.valueOf(status) } catch (e: Exception) { QnAStatus.ERROR },
            createdAt = createdAt,
            answeredAt = answeredAt
        )
    }
}
