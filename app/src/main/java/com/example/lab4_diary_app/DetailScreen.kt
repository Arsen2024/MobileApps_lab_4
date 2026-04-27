package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.viewmodel.DetailState
import com.example.lab4_diary_app.viewmodel.DetailViewModel
import com.example.lab4_diary_app.viewmodel.DetailViewModelFactory

@Composable
fun DetailScreen(itemId: String, repository: AppRepository) {
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(repository, itemId)
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        DetailState.Loading -> {
            Column(
                modifier = Modifier
                    .padding(32.dp),
            ) {
                CircularProgressIndicator()
                Text(
                    "Завантаження...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        is DetailState.Error -> {
            val message = (state as DetailState.Error).message

            Column(Modifier.padding(16.dp)) {
                Text(
                    "Помилка: $message",
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    "Спробуйте ще раз",
                    modifier = Modifier
                        .padding(top = 12.dp)
                )

                androidx.compose.material3.Button(
                    onClick = { viewModel.retry() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Retry")
                }
            }
        }

        is DetailState.Success -> {
            val item = (state as DetailState.Success).item
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

    }

}