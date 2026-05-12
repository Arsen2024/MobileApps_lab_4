package com.example.lab4_diary_app.data

import com.example.lab4_diary_app.data.local.DiaryEntity
import com.example.lab4_diary_app.network.DiaryDto

fun DiaryEntity.toDiaryItem(): DiaryItem {
    return DiaryItem(
        id = id,
        title = title,
        description = description,
        isFavorite = isFavorite,
        createdAt = createdAt,
        priority = priority,
        category = category,
        mood = mood,
        photoUri = photoUri,
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        locationTime = locationTime
    )
}

fun DiaryItem.toDiaryEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        title = title,
        description = description,
        isFavorite = isFavorite,
        createdAt = createdAt,
        priority = priority,
        category = category,
        mood = mood,
        photoUri = photoUri,
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        locationTime = locationTime
    )
}

fun DiaryDto.toEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        title = title,
        description = description,
        isFavorite = isFavorite,
        createdAt = createdAt,
        priority = priority,
        category = category,
        mood = mood,
        photoUri = null
    )
}

fun DiaryEntity.toDto(): DiaryDto {
    return DiaryDto(
        id = id,
        title = title,
        description = description,
        isFavorite = isFavorite,
        createdAt = createdAt,
        priority = priority,
        category = category,
        mood = mood,
        photoUri = null
    )
}