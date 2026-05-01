package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val prefs: UserPreferencesRepository) : ViewModel() {
    val name: StateFlow<String> =
        prefs.userNameFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    fun updateName(newName: String) {
        viewModelScope.launch {
            prefs.saveUserName(newName)
        }
    }
}