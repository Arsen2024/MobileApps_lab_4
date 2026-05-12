package com.example.lab4_diary_app.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab4_diary_app.constants.TARGET_LAT
import com.example.lab4_diary_app.constants.TARGET_LON
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

                repository.getItemById(itemId).collect { item ->
                    if (item != null) {
                        _state.value = DetailState.Success(item)
                    } else {
                        _state.value = DetailState.Error("Запис не знайдено")
                    }
                }
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

    fun onPhotoTaken(uri: String) {
        viewModelScope.launch {
            try {
                val currentState = _state.value

                if (currentState is DetailState.Success) {
                    val updatedItem = currentState.item.copy(
                        photoUri = uri
                    )

                    repository.updateItem(updatedItem)

                    _state.value = DetailState.Success(updatedItem)
                }

            } catch (e: Exception) {
                _state.value = DetailState.Error(
                    e.message ?: "Помилка збереження фото"
                )
            }
        }
    }

    fun onLocationTaken(
        lat: Double,
        lon: Double,
        accuracy: Float,
        time: Long
    ) {
        Log.d("LOCATION_VM", "onLocationTaken CALLED: lat=$lat lon=$lon acc=$accuracy time=$time")

        viewModelScope.launch {
            val current = _state.value
            Log.d("LOCATION_VM", "current state = $current")
            if (current is DetailState.Success) {
                val updated = current.item.copy(
                    latitude = lat,
                    longitude = lon,
                    accuracy = accuracy,
                    locationTime = time
                )

                Log.d("LOCATION_VM", "UPDATED ITEM = $updated")
                repository.updateItem(updated)
                _state.value = DetailState.Success(updated)
                Log.d("LOCATION_VM", "STATE UPDATED SUCCESS")
            } else {
                Log.d("LOCATION_VM", "STATE NOT SUCCESS -> SKIP UPDATE")
            }
        }
    }

    fun calculateDistance(lat: Double, lon: Double): Float {
        val results = FloatArray(1)

        Location.distanceBetween(
            lat,
            lon,
            TARGET_LAT,
            TARGET_LON,
            results
        )

        return results[0]
    }

    val distance: Float?
        get() {
            val current = _state.value as? DetailState.Success ?: return null

            val lat = current.item.latitude ?: return null
            val lon = current.item.longitude ?: return null

            return calculateDistance(lat, lon)
        }
}