package com.example.lab4_diary_app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.viewmodel.ListState
import com.example.lab4_diary_app.viewmodel.ListViewModel

@Composable
fun ListScreen(modifier: Modifier = Modifier, viewModel: ListViewModel, onItemClick: (DiaryItem) -> Unit, windowSizeClass: WindowSizeClass) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val deletingIds by viewModel.deletingIds.collectAsState()
    val isTablet = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    var selectedItem by remember { mutableStateOf<DiaryItem?>(null) }

    var filter by remember { mutableStateOf(ListFilter.ALL) }

    if(!isTablet) {
        Column(modifier = modifier) {
            FilterRow { filter = it }
            ListContent(state, viewModel, deletingIds, filter, onItemClick)
        }
    } else {
        Row(modifier = modifier.fillMaxWidth()) {

            Column(modifier = Modifier.weight(1f)) {
                FilterRow { filter = it }

                ListContent(
                    state,
                    viewModel,
                    deletingIds,
                    filter
                ) { item ->
                    selectedItem = item
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                if (selectedItem == null) {
                    Text("Оберіть елемент зі списку")
                } else {
                    Text("Деталі", modifier = Modifier.padding(bottom = 8.dp))
                    Text(selectedItem!!.title)
                    Spacer(Modifier.height(8.dp))
                    Text(selectedItem!!.description)
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    onChange: (ListFilter) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { onChange(ListFilter.ALL) }) { Text("Всі") }
        Button(onClick = { onChange(ListFilter.SHORT) }) { Text("Короткі") }
        Button(onClick = { onChange(ListFilter.LONG) }) { Text("Довгі") }
    }
}

@Composable
private fun ListContent(
    state: ListState,
    viewModel: ListViewModel,
    deletingIds: Set<String>,
    filter: ListFilter,
    onItemClick: (DiaryItem) -> Unit
) {
    when(state) {
        is ListState.Loading -> {
            Text("Завантаження...", modifier = Modifier.padding(16.dp))
        }

        is ListState.Error -> {
            Column(Modifier.padding(16.dp)) {
                Text("Помилка: ${state.message}")
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
            val items = state.items
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
                DiaryListContent(items, filter, viewModel, deletingIds, onItemClick)
            }
        }

        is ListState.Success -> {
            val items = state.items

            DiaryListContent(items, filter, viewModel, deletingIds, onItemClick)
        }
    }
}

@Composable
private fun DiaryListContent(
    items: List<DiaryItem>,
    filter: ListFilter,
    viewModel: ListViewModel,
    deletingIds: Set<String>,
    onItemClick: (DiaryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredItems by remember(items, filter) {
        derivedStateOf {
            when (filter) {
                ListFilter.ALL -> items
                ListFilter.SHORT -> items.filter { it.title.length <= 7 }
                ListFilter.LONG -> items.filter { it.title.length > 7 }
            }
        }
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(filteredItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { onItemClick(item) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row {
                        Text(item.title)

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = if (item.isFavorite) "⭐" else "☆",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    viewModel.toggleFavorite(item)
                                }
                        )

                        val isDeleting = deletingIds.contains(item.id)
                        Text(
                            text = "🗑️",
                            modifier = Modifier
                                .alpha(if (isDeleting) 0.4f else 1f)
                                .clickable(enabled = !isDeleting) {
                                viewModel.deleteItem(item.id)
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
        }
    }
}