package com.example.lab4_diary_app.data


class AppRepository {
    private val diaryItems = listOf(
        DiaryItem(1, "Запис 1", "Опис 1"),
        DiaryItem(2, "Запис 2", "Опис 2"),
        DiaryItem(3, "Запис 3", "Опис 3"),
        DiaryItem(4, "Запис 4", "Опис 4"),
        DiaryItem(5, "Запис 5", "Опис 5"),
        DiaryItem(6, "Запис 6", "Опис 6")
    )

    fun getAllItems(): List<DiaryItem> = diaryItems

    fun getItemById(id: Int): DiaryItem? =
        diaryItems.find { it.id == id }
}