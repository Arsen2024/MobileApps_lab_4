package com.example.lab4_diary_app.data

import android.content.Context
import android.net.Uri
import com.example.lab4_diary_app.data.local.DiaryDao
import com.example.lab4_diary_app.network.DiaryApi
import com.example.lab4_diary_app.network.DiaryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import androidx.core.net.toUri


class AppRepository(private val diaryDao: DiaryDao, private val api: DiaryApi) {
    fun getAllItems(): Flow<List<DiaryItem>> =
        diaryDao.getAllEntries()
            .map { list -> list.map { it.toDiaryItem() } }

    fun getItemById(id: String): Flow<DiaryItem?> =
        diaryDao.getById(id)
            .map { entity -> entity?.toDiaryItem() }

    fun createImageFile(context: Context): File {
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()

        return File(
            dir,
            "IMG_${System.currentTimeMillis()}.jpg"
        )
    }

    suspend fun getRemoteById(id: String): DiaryItem {
        return api.getEntryById(id).toEntity().toDiaryItem()
    }

    fun getFavoriteItems(): Flow<List<DiaryItem>> =
        diaryDao.getFavoriteEntries().map { it.map { e -> e.toDiaryItem() } }

    suspend fun setFavorite(id: String, value: Boolean) =
        diaryDao.setFavorite(id, value)

    suspend fun refresh() {
        val remote = api.getEntries()

        val savedPhotos = diaryDao.getAllPhotoUris()
            .associate { it.id to it.photoUri }
        val savedLocations = diaryDao.getAllLocations()
            .associateBy { it.id }

        diaryDao.clear()

        val merged = remote.map { dto ->
            val entity = dto.toEntity()
            val location = savedLocations[entity.id]

            entity.copy(
                photoUri = savedPhotos[entity.id],
                latitude = location?.latitude,
                longitude = location?.longitude,
                accuracy = location?.accuracy,
                locationTime = location?.locationTime
            )
        }
        diaryDao.insertAll(merged)
    }

    suspend fun addRemote(item: DiaryDto) {
        val created = api.createEntry(item)
        diaryDao.insertEntry(created.toEntity())
    }

    suspend fun updateItem(item: DiaryItem) {
        diaryDao.updateEntry(item.toDiaryEntity())
    }

    suspend fun deleteRemote(id: String) {
        val photoUri = diaryDao.getPhotoUriById(id)

        api.deleteEntry(id)
        diaryDao.deleteById(id)

        photoUri?.let {
            try {
                val file = File(it)
                if (file.exists()) file.delete()
            } catch (e: Exception) { }
        }
    }

    suspend fun toggleFavoriteRemote(item: DiaryItem) {
        val updated = DiaryDto(
            id = item.id,
            title = item.title,
            description = item.description,
            isFavorite = !item.isFavorite,
            createdAt = item.createdAt,
            priority = item.priority,
            category = item.category,
            mood = item.mood,
            photoUri = item.photoUri
        )

        api.updateEntry(item.id, updated)
        refresh()
    }
}