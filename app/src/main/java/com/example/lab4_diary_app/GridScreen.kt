package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun GridScreen(modifier: Modifier = Modifier, viewModel: ListViewModel = viewModel(), onItemClick: (DiaryItem) -> Unit) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var sort by remember { mutableStateOf(GridSort.ASC) }

    val sortedItems by remember(items, sort) {
        derivedStateOf {
            when (sort) {
                GridSort.ASC -> items.sortedBy { it.title }
                GridSort.DESC -> items.sortedByDescending { it.title }
            }
        }
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { sort = GridSort.ASC }) { Text("A-Z") }
            Button(onClick = { sort = GridSort.DESC }) { Text("Z-A") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text("Завантаження...", modifier = Modifier.padding(16.dp))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(16.dp)
            ) {
                items(sortedItems) { item ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
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