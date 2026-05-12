package com.example.lab4_diary_app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(time: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(time))
}