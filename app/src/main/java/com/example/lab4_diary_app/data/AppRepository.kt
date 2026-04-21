package com.example.lab4_diary_app.data

import com.example.lab4_diary_app.data.local.DiaryDao
import com.example.lab4_diary_app.data.local.DiaryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AppRepository(private val diaryDao: DiaryDao) {
    fun getAllItems(): Flow<List<DiaryItem>> =
        diaryDao.getAllEntries()
            .map { list -> list.map { it.toDiaryItem() } }

    fun getItemById(id: Int): Flow<DiaryItem?> =
        diaryDao.getById(id)
            .map { entity -> entity?.toDiaryItem() }

    fun getFavoriteItems(): Flow<List<DiaryItem>> =
        diaryDao.getFavoriteEntries().map { it.map { e -> e.toDiaryItem() } }

    suspend fun insert(item: DiaryItem) =
        diaryDao.insertEntry(item.toDiaryEntity())

    suspend fun delete(item: DiaryItem) =
        diaryDao.deleteEntry(item.toDiaryEntity())

    suspend fun update(item: DiaryItem) =
        diaryDao.updateEntry(item.toDiaryEntity())

    suspend fun setFavorite(id: Int, value: Boolean) =
        diaryDao.setFavorite(id, value)
}