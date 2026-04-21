package com.example.lab4_diary_app.data

data class DiaryItem(
    val id: Int,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val createdAt: Long
)
