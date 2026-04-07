package com.example.lab4_diary_app

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab4_diary_app.data.DiaryItem
import com.example.lab4_diary_app.viewmodel.ListViewModel

@Composable
fun ListScreen(modifier: Modifier = Modifier, viewModel: ListViewModel = viewModel(), onItemClick: (DiaryItem) -> Unit) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var filter by remember { mutableStateOf(ListFilter.ALL) }

    val filteredItems by remember(items, filter) {
        derivedStateOf {
            when (filter) {
                ListFilter.ALL -> items
                ListFilter.SHORT -> items.filter { it.title.length <= 7 }
                ListFilter.LONG -> items.filter { it.title.length > 7 }
            }
        }
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { filter = ListFilter.ALL }) { Text("Всі") }
            Button(onClick = { filter = ListFilter.SHORT }) { Text("Короткі") }
            Button(onClick = { filter = ListFilter.LONG }) { Text("Довгі") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text("Завантаження...", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(filteredItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = { onItemClick(item) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.title)
                            Text(item.description)
                        }
                    }
                }
            }
        }
    }
}