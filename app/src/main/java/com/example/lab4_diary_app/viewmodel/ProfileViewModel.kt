package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(initialName: String) : ViewModel() {
    private val _name = MutableStateFlow(initialName)
    val name: StateFlow<String> = _name

    fun updateName(newName: String) {
        _name.value = newName
    }
}