package com.example.lab4_diary_app.data

import com.example.lab4_diary_app.data.local.DiaryDao
import com.example.lab4_diary_app.network.DiaryApi
import com.example.lab4_diary_app.network.DiaryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AppRepository(private val diaryDao: DiaryDao, private val api: DiaryApi) {
    fun getAllItems(): Flow<List<DiaryItem>> =
        diaryDao.getAllEntries()
            .map { list -> list.map { it.toDiaryItem() } }

    fun getItemById(id: String): Flow<DiaryItem?> =
        diaryDao.getById(id)
            .map { entity -> entity?.toDiaryItem() }

    suspend fun getRemoteById(id: String): DiaryItem {
        return api.getEntryById(id).toEntity().toDiaryItem()
    }

    fun getFavoriteItems(): Flow<List<DiaryItem>> =
        diaryDao.getFavoriteEntries().map { it.map { e -> e.toDiaryItem() } }

    suspend fun setFavorite(id: String, value: Boolean) =
        diaryDao.setFavorite(id, value)

    suspend fun refresh() {
        val remote = api.getEntries()

        diaryDao.clear()
        diaryDao.insertAll(remote.map { it.toEntity() })
    }

    suspend fun addRemote(item: DiaryDto) {
        val created = api.createEntry(item)
        diaryDao.insertEntry(created.toEntity())
    }

    suspend fun deleteRemote(id: String) {
        api.deleteEntry(id)
        diaryDao.deleteById(id)
    }

    suspend fun toggleFavoriteRemote(item: DiaryItem) {
        val updated = DiaryDto(
            id = item.id,
            title = item.title,
            description = item.description,
            isFavorite = !item.isFavorite,
            createdAt = item.createdAt
        )

        api.updateEntry(item.id, updated)
        refresh()
    }
}