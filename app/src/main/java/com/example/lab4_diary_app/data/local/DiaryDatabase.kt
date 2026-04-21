package com.example.lab4_diary_app.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [DiaryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao() : DiaryDao

    companion object {
        @Volatile
        private var INSTANCE: DiaryDatabase? = null

        fun getDatabase(context: Context): DiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiaryDatabase::class.java,
                    "diary_db"
                )
                    .addCallback(seedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun seedCallback() =
            object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = INSTANCE?.diaryDao() ?: return@launch

                        dao.insertEntry(
                            DiaryEntity(
                                title = "Перша замітка",
                                description = "Це тестовий запис",
                                isFavorite = false
                            )
                        )

                        dao.insertEntry(
                            DiaryEntity(
                                title = "Навчання",
                                description = "Вивчаю Jetpack Compose",
                                isFavorite = true
                            )
                        )

                        dao.insertEntry(
                            DiaryEntity(
                                title = "Завдання",
                                description = "Зробити лабораторну",
                                isFavorite = false
                            )
                        )

                        dao.insertEntry(
                            DiaryEntity(
                                title = "Ідея",
                                description = "Зробити todo app",
                                isFavorite = true
                            )
                        )

                        dao.insertEntry(
                            DiaryEntity(
                                title = "Нотатка",
                                description = "Seed data працює!",
                                isFavorite = false
                            )
                        )
                    }
                    Log.d("SEED", "INSERT START")
                }
            }
    }

}