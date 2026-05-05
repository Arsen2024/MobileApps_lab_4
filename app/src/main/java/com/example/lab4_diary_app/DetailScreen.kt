package com.example.lab4_diary_app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.viewmodel.DetailState
import com.example.lab4_diary_app.viewmodel.DetailViewModel
import com.example.lab4_diary_app.viewmodel.DetailViewModelFactory

@Composable
fun DetailScreen(itemId: String, repository: AppRepository, modifier: Modifier = Modifier) {
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(repository, itemId)
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(500)
    )

    val headerColor by animateColorAsState(
        targetValue = if (expanded)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(500)
    )

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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clickable { expanded = !expanded },
                    colors = CardDefaults.cardColors(containerColor = headerColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Додаткова інформація",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(tween(500)) + fadeIn(tween(500)),
                    exit = shrinkVertically(tween(500)) + fadeOut(tween(500))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Пріоритет: ${item.priority}")
                        Text("Категорія: ${item.category}")
                        Text("Настрій: ${item.mood}")
                        Text("Обране: ${if (item.isFavorite) "Так" else "Ні"}")
                    }
                }
            }
        }

    }

}