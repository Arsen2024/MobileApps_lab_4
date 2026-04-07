package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface DetailState {
    object Loading : DetailState
    data class Success(val item: DiaryItem) : DetailState
    data class Error(val message: String) : DetailState
}

class DetailViewModel(private val repository: AppRepository, private val itemId: Int) : ViewModel() {
    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> = _state

    init {
        loadItem()
    }

    private fun loadItem() {
        val item = repository.getItemById(itemId)
        _state.value = if (item != null) {
            DetailState.Success(item)
        } else {
            DetailState.Error("Запис не знайдено")
        }
    }

}