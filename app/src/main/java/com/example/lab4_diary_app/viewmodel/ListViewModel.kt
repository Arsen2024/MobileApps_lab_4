package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.SortOrder
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository
import com.example.lab4_diary_app.network.DiaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface ListState {
    object Loading: ListState
    data class Success(val items: List<DiaryItem>) : ListState
    data class Error(val message: String) : ListState
    object Empty : ListState
    object Offline : ListState
}

class ListViewModel(private val repository: AppRepository, private val preferences: UserPreferencesRepository) : ViewModel() {

    private val _state = MutableStateFlow<ListState>(ListState.Loading)
    val state: StateFlow<ListState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = ListState.Loading

                repository.refresh()

                combine(
                    repository.getAllItems(),
                    preferences.sortOrderFlow,
                    preferences.showOnlyFavoritesFlow
                ) { items, sort, onlyFav ->

                    var result = items

                    if (onlyFav) {
                        result = result.filter { it.isFavorite }
                    }

                    result = when (sort) {
                        SortOrder.NEWEST -> result.sortedByDescending { it.createdAt }
                        SortOrder.OLDEST -> result.sortedBy { it.createdAt }
                    }

                    result
                }.collect { items ->

                    _state.value = when {
                        items.isEmpty() -> ListState.Empty
                        else -> ListState.Success(items)
                    }
                }

            } catch (e: Exception) {
                _state.value = ListState.Error(
                    e.message ?: "Помилка завантаження"
                )
            }
        }
    }

    fun retry() {
        loadData()
    }

    fun toggleFavorite(item: DiaryItem) {
        viewModelScope.launch {
            repository.toggleFavoriteRemote(item)
        }
    }

    fun addItem(title: String, description: String) {
        viewModelScope.launch {
            repository.addRemote(
                DiaryDto(
                    id = "",
                    title = title,
                    description = description,
                    isFavorite = false,
                    createdAt = System.currentTimeMillis()
                )
            )
            loadData()
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteRemote(id)
            } catch (e: Exception) {
                _state.value = ListState.Error(
                    e.message ?: "Помилка видалення"
                )
            }
        }
    }
}