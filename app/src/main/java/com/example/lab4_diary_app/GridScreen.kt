package com.example.lab4_diary_app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.viewmodel.ListState
import com.example.lab4_diary_app.viewmodel.ListViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun GridScreen(modifier: Modifier = Modifier, viewModel: ListViewModel, onItemClick: (DiaryItem) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var sort by remember { mutableStateOf(GridSort.NONE) }


    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { sort = GridSort.ASC }) { Text("A-Z") }
            Button(onClick = { sort = GridSort.DESC }) { Text("Z-A") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when(state) {
            is ListState.Loading -> {
                Text("Завантаження...", modifier = Modifier.padding(16.dp))
            }

            is ListState.Error -> {
                val message = (state as ListState.Error).message
                Column(Modifier.padding(16.dp)) {
                    Text("Помилка: $message")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Спробувати ще раз")
                    }
                }
            }

            is ListState.Empty -> {
                Text("Список порожній", modifier = Modifier.padding(16.dp))
            }

            is ListState.Offline -> {
                val items = (state as ListState.Offline).items

                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Офлайн режим. Показано кешовані дані",
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    DiaryGridContent(items, sort, viewModel, onItemClick)
                }
            }

            is ListState.Success -> {
                val items = (state as ListState.Success).items

                DiaryGridContent(items, sort, viewModel, onItemClick)
            }
        }
    }
}

@Composable
private fun DiaryGridContent(
    items: List<DiaryItem>,
    sort: GridSort,
    viewModel: ListViewModel,
    onItemClick: (DiaryItem) -> Unit
) {
    val sortedItems by remember(items, sort) {
        derivedStateOf {
            when (sort) {
                GridSort.NONE -> items
                GridSort.ASC -> items.sortedBy { it.title }
                GridSort.DESC -> items.sortedByDescending { it.title }
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        items(
            items = sortedItems,
            key = { it.id }
        ) { item ->
            var offsetX by remember { mutableFloatStateOf(0f) }
            var isVisible by remember { mutableStateOf(false) }
            var shouldDelete by remember { mutableStateOf(false) }
            var menuExpanded by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                isVisible = true
            }

            LaunchedEffect(shouldDelete) {
                if (shouldDelete) {
                    delay(600)
                    viewModel.deleteItem(item.id)
                }
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(700)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(700)
                        ),
                exit = fadeOut(tween(600)) +
                        slideOutVertically(
                            targetOffsetY = { it / 2 },
                            animationSpec = tween(600)
                        )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        Text(
                            text = "Видалити",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(16.dp)
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(offsetX.roundToInt(), 0) }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        if (offsetX < -300f) {
                                            isVisible = false
                                            shouldDelete = true
                                        } else {
                                            offsetX = 0f
                                        }
                                    }
                                ) { _, dragAmount ->
                                    offsetX = (offsetX + dragAmount).coerceAtMost(0f)
                                }
                            }
                            .combinedClickable(
                                onClick = { onItemClick(item) },
                                onLongClick = { menuExpanded = true }
                            )
                    ) {

                        Column(Modifier.padding(16.dp)) {

                            Row {
                                Text(item.title)

                                Spacer(Modifier.weight(1f))

                                Text(
                                    text = if (item.isFavorite) "⭐" else "☆",
                                    modifier = Modifier.clickable {
                                        viewModel.toggleFavorite(item)
                                    }
                                )
                            }

                            Text(item.description)

                            Spacer(Modifier.height(8.dp))

                            Text("Пріоритет: ${item.priority}")
                            Text("Категорія: ${item.category}")
                            Text("Настрій: ${item.mood}")
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (item.isFavorite)
                                        "Видалити з обраного"
                                    else
                                        "Додати до обраного"
                                )
                            },
                            onClick = {
                                viewModel.toggleFavorite(item)
                                menuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Видалити") },
                            onClick = {
                                viewModel.deleteItem(item.id)
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}