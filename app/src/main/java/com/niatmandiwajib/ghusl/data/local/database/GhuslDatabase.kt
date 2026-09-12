package com.niatmandiwajib.ghusl.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.niatmandiwajib.ghusl.data.local.dao.BookmarkDao
import com.niatmandiwajib.ghusl.data.local.dao.ReadHistoryDao
import com.niatmandiwajib.ghusl.data.local.dao.QnADao
import com.niatmandiwajib.ghusl.data.local.entity.BookmarkEntity
import com.niatmandiwajib.ghusl.data.local.entity.ReadHistoryEntity
import com.niatmandiwajib.ghusl.data.local.entity.QnAEntity

@Database(
    entities = [BookmarkEntity::class, ReadHistoryEntity::class, QnAEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GhuslDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readHistoryDao(): ReadHistoryDao
    abstract fun qnADao(): QnADao

    companion object {
        @Volatile
        private var INSTANCE: GhuslDatabase? = null

        fun getInstance(context: Context): GhuslDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GhuslDatabase::class.java,
                    "ghusl_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
