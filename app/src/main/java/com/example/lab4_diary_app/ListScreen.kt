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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.viewmodel.ListState
import com.example.lab4_diary_app.viewmodel.ListViewModel

@Composable
fun ListScreen(modifier: Modifier = Modifier, viewModel: ListViewModel, onItemClick: (DiaryItem) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var filter by remember { mutableStateOf(ListFilter.ALL) }


    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { filter = ListFilter.ALL }) { Text("Всі") }
            Button(onClick = { filter = ListFilter.SHORT }) { Text("Короткі") }
            Button(onClick = { filter = ListFilter.LONG }) { Text("Довгі") }
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
                Text("Немає підключення до мережі", modifier = Modifier.padding(16.dp))
            }

            is ListState.Success -> {
                val items = (state as ListState.Success).items

                val filteredItems by remember(items, filter) {
                    derivedStateOf {
                        when (filter) {
                            ListFilter.ALL -> items
                            ListFilter.SHORT -> items.filter { it.title.length <= 7 }
                            ListFilter.LONG -> items.filter { it.title.length > 7 }
                        }
                    }
                }

                LazyColumn(modifier = Modifier.padding(16.dp)) {
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

                                    Text(
                                        text = "🗑️",
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.deleteItem(item.id)
                                            }
                                    )
                                }
                                Text(item.description)
                            }
                        }
                    }
                }
            }
        }
    }
}