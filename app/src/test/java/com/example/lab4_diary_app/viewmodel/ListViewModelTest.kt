package com.example.lab4_diary_app.viewmodel

import app.cash.turbine.test
import com.example.lab4_diary_app.SortOrder
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: AppRepository
    private lateinit var preferences: UserPreferencesRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository = mockk()
        preferences = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadDataSuccessReturnsSuccessState() = runTest {
        //Arrange
        val items = listOf(
            DiaryItem(
                id = "1",
                title = "Test",
                description = "Desc",
                isFavorite = false,
                createdAt = 1L,
                priority = 1,
                category = "Work",
                mood = 1,
                photoUri = null
            )
        )

        coEvery { repository.refresh() } returns Unit
        every { repository.getAllItems() } returns flowOf(items)

        every { preferences.sortOrderFlow } returns MutableStateFlow(SortOrder.NEWEST)
        every { preferences.showOnlyFavoritesFlow } returns MutableStateFlow(false)

        //Act
        val viewModel = ListViewModel(repository, preferences)

        dispatcher.scheduler.advanceUntilIdle()

        //Assert
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is ListState.Success)
        }
    }

    @Test
    fun emptyListReturnsEmptyState() = runTest {
        // Arrange
        coEvery { repository.refresh() } returns Unit
        every { repository.getAllItems() } returns flowOf(emptyList())

        every { preferences.sortOrderFlow } returns MutableStateFlow(SortOrder.NEWEST)
        every { preferences.showOnlyFavoritesFlow } returns MutableStateFlow(false)

        // Act
        val viewModel = ListViewModel(repository, preferences)

        dispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is ListState.Empty)
        }
    }

    @Test
    fun networkErrorWithCachedDataReturnsOfflineState() = runTest {
        //Arrange
        val items = listOf(
            DiaryItem(
                id = "1",
                title = "Offline",
                description = "Cached",
                isFavorite = false,
                createdAt = 1L,
                priority = 1,
                category = "Work",
                mood = 1,
                photoUri = null
            )
        )

        coEvery { repository.refresh() } throws Exception("No internet")

        every { repository.getAllItems() } returns flowOf(items)

        every { preferences.sortOrderFlow } returns MutableStateFlow(SortOrder.NEWEST)
        every { preferences.showOnlyFavoritesFlow } returns MutableStateFlow(false)

        // Act
        val viewModel = ListViewModel(repository, preferences)

        dispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is ListState.Offline)
        }
    }
}