package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val prefs: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}