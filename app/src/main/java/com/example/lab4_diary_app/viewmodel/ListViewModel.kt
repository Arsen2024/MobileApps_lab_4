package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.SortOrder
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListViewModel(private val repository: AppRepository, preferences: UserPreferencesRepository) : ViewModel() {
    val items: StateFlow<List<DiaryItem>> =
        combine(
            repository.getAllItems(),
            preferences.sortOrderFlow,
            preferences.showOnlyFavoritesFlow,
        ) { items, sort, onlyFav ->
            var result = items

            if(onlyFav) {
                result = result.filter { it.isFavorite }
            }

            result = when (sort) {
                SortOrder.NEWEST -> result.sortedByDescending { it.createdAt }
                SortOrder.OLDEST -> result.sortedBy { it.createdAt }
            }

            result
        }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun toggleFavorite(item: DiaryItem) {
        viewModelScope.launch {
            repository.setFavorite(item.id, !item.isFavorite)
        }
    }
}