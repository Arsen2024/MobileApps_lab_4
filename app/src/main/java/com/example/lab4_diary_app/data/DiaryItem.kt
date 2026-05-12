package com.example.lab4_diary_app.data

data class DiaryItem(
    val id: String,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val priority: Int,
    val category: String,
    val mood: Int,
    val photoUri: String?,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracy: Float? = null,
    val locationTime: Long? = null,
)
