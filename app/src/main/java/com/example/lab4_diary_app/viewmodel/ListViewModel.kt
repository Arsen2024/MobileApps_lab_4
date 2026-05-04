package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.SortOrder
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository
import com.example.lab4_diary_app.network.DiaryDto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface ListState {
    object Loading: ListState
    data class Success(val items: List<DiaryItem>) : ListState
    data class Error(val message: String) : ListState
    object Empty : ListState
    data class Offline(val items: List<DiaryItem>) : ListState
}

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    object NavigateBack : UiEvent
}

class ListViewModel(val repository: AppRepository, private val preferences: UserPreferencesRepository) : ViewModel() {

    private val _state = MutableStateFlow<ListState>(ListState.Loading)
    val state: StateFlow<ListState> = _state

    private val _event = Channel<UiEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val _navigation = Channel<UiEvent>(Channel.BUFFERED)
    val navigation = _navigation.receiveAsFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds

    private var isOffline = false

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = ListState.Loading

                try {
                    repository.refresh()
                    isOffline = false
                } catch (e: Exception) {
                    isOffline = true
                }

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
                        items.isEmpty() && isOffline -> ListState.Error("Немає інтернету і кеш порожній")
                        items.isEmpty() -> ListState.Empty
                        isOffline -> ListState.Offline(items)
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
            runCatching { repository.toggleFavoriteRemote(item) }
        }
    }

    fun addItem(title: String, description: String, isFavorite: Boolean, priority: Int, category: String, mood: Int) {
        viewModelScope.launch {
            try {
                repository.addRemote(
                    DiaryDto(
                        id = "",
                        title = title,
                        description = description,
                        isFavorite = isFavorite,
                        createdAt = System.currentTimeMillis(),
                        priority = priority,
                        category = category,
                        mood = mood
                    )
                )

                _navigation.send(UiEvent.NavigateBack)
                _event.send(UiEvent.ShowSnackbar("Запис успішно додано"))
                loadData()

            } catch (e: Exception) {
                _navigation.send(UiEvent.NavigateBack)
                _event.send(UiEvent.ShowSnackbar("Помилка додавання"))
            }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            _deletingIds.value += id

            try {
                repository.deleteRemote(id)
                _event.send(UiEvent.ShowSnackbar("Запис видалено"))
                loadData()

            } catch (e: Exception) {
                _event.send(UiEvent.ShowSnackbar("Помилка видалення"))
            } finally {
                _deletingIds.value -= id
            }
        }
    }
}