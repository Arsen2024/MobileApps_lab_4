package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository

@Suppress("UNCHECKED_CAST")
class ListViewModelFactory(private val repository: AppRepository, private val preferences: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListViewModel(repository, preferences) as T
    }
}