package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
fun DetailScreen(itemId: Int, repository: AppRepository) {
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(repository, itemId)
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is DetailState.Loading -> CircularProgressIndicator()
        is DetailState.Success -> {
            val item = (state as DetailState.Success).item
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = item.title, modifier = Modifier.padding(bottom = 8.dp))
                Text(text = item.description)
            }
        }
        is DetailState.Error -> {
            Text((state as DetailState.Error).message)
        }
    }

}