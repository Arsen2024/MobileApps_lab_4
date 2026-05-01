package com.example.lab4_diary_app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.lab4_diary_app.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val SHOW_ONLY_FAVORITES = booleanPreferencesKey("show_only_favorites")
    }

    val userNameFlow: Flow<String> = context.dataStore.data
        .map { it[USER_NAME] ?: "" }

    val sortOrderFlow: Flow<SortOrder> = context.dataStore.data
        .map { prefs ->
            SortOrder.valueOf(prefs[SORT_ORDER] ?: SortOrder.NEWEST.name)
        }

    val showOnlyFavoritesFlow: Flow<Boolean> = context.dataStore.data
        .map { it[SHOW_ONLY_FAVORITES] ?: false }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit {
            it[USER_NAME] = name
        }
    }

    suspend fun saveSortOrder(order: SortOrder) {
        context.dataStore.edit {
            it[SORT_ORDER] = order.name
        }
    }

    suspend fun saveShowOnlyFavorites(value: Boolean) {
        context.dataStore.edit {
            it[SHOW_ONLY_FAVORITES] = value
        }
    }
}