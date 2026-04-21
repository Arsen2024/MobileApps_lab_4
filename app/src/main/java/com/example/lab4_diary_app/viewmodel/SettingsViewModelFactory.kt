package com.example.lab4_diary_app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val repository = UserPreferencesRepository(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository) as T
    }
}