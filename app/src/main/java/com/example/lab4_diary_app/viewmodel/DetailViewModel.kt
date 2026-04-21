package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface DetailState {
    object Loading : DetailState
    data class Success(val item: DiaryItem) : DetailState
    data class Error(val message: String) : DetailState
}

class DetailViewModel(repository: AppRepository, itemId: Int) : ViewModel() {
    val state: StateFlow<DetailState> =
        repository.getItemById(itemId)
            .map { item ->
                if (item != null) {
                    DetailState.Success(item)
                } else {
                    DetailState.Error("Запис не знайдено")
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DetailState.Loading
            )

}