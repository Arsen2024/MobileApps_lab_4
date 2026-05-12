package com.example.lab4_diary_app

data class IdLocation(
    val id: String,
    val latitude: Double?,
    val longitude: Double?,
    val accuracy: Float?,
    val locationTime: Long?
)
