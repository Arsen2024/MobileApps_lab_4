package com.example.lab4_diary_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val priority: Int,
    val category: String,
    val mood: Int
)
