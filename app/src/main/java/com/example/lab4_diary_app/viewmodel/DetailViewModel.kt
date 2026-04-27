package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailState {
    object Loading : DetailState
    data class Success(val item: DiaryItem) : DetailState
    data class Error(val message: String) : DetailState
}

class DetailViewModel(private val repository: AppRepository, private val itemId: String) : ViewModel() {
    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        loadItem()
    }
    private fun loadItem() {
        viewModelScope.launch {
            try {
                _state.value = DetailState.Loading

                val item = repository.getRemoteById(itemId)

                _state.value = DetailState.Success(item)

            } catch (e: Exception) {
                _state.value = DetailState.Error(
                    e.message ?: "Помилка завантаження"
                )
            }
        }
    }

    fun retry() {
        loadItem()
    }
}