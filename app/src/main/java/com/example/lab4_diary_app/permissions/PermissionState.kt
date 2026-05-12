package com.example.lab4_diary_app.permissions

sealed class PermissionState {
    object Granted : PermissionState()
    object Denied : PermissionState()
    object PermanentlyDenied : PermissionState()
}