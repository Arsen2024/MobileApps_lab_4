package com.example.lab4_diary_app.network

data class DiaryDto(
    val id: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val createdAt: Long
)
