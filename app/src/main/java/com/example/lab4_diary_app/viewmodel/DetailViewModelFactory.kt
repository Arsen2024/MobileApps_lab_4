package com.example.lab4_diary_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab4_diary_app.data.AppRepository

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(private val repository: AppRepository, private val itemId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository, itemId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}