package com.example.lab4_diary_app.data

import com.example.lab4_diary_app.data.local.DiaryEntity

fun DiaryEntity.toDiaryItem(): DiaryItem {
    return DiaryItem(
        id = id,
        title = title,
        description = description,
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}

fun DiaryItem.toDiaryEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        title = title,
        description = description,
        isFavorite = isFavorite,
        createdAt = createdAt
    )
}