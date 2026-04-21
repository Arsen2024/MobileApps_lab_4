package com.example.lab4_diary_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
