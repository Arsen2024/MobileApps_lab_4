package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListViewModel(private val repository: AppRepository = AppRepository()) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _items = MutableStateFlow<List<DiaryItem>>(emptyList())
    val items: StateFlow<List<DiaryItem>> = _items

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            delay(700)
            _items.value = repository.getAllItems()
            _isLoading.value = false
        }
    }
}