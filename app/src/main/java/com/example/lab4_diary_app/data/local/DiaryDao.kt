package com.example.lab4_diary_app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary WHERE id = :id")
    fun getById(id: String): Flow<DiaryEntity?>

    @Query("UPDATE diary SET isFavorite = :value WHERE id = :id")
    suspend fun setFavorite(id: String, value: Boolean)

    @Query("SELECT * FROM diary WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteEntries(): Flow<List<DiaryEntity>>

    @Query("DELETE FROM diary")
    suspend fun clear()

    @Query("DELETE FROM diary WHERE id = :id")
    suspend fun deleteById(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DiaryEntity>)

    @Delete
    suspend fun deleteEntry(entry: DiaryEntity)

    @Update
    suspend fun updateEntry(entry: DiaryEntity)
}