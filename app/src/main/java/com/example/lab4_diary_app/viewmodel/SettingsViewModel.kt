package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.SortOrder
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {
    val userName = repository.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val sortOrder = repository.sortOrderFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.NEWEST)

    val showOnlyFavorites = repository.showOnlyFavoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun saveName(name: String) {
        viewModelScope.launch {
            repository.saveUserName(name)
        }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch {
            repository.saveSortOrder(order)
        }
    }

    fun setShowOnlyFavorites(value: Boolean) {
        viewModelScope.launch {
            repository.saveShowOnlyFavorites(value)
        }
    }
}