package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab4_diary_app.viewmodel.ProfileViewModel
import com.example.lab4_diary_app.viewmodel.ProfileViewModelFactory

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, initialName: String = "", viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(initialName))) {
    val name by viewModel.name.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Diary App", style = MaterialTheme.typography.headlineMedium)
        Text("Version 1.0", style = MaterialTheme.typography.headlineMedium)
        Text("Developer: Arsenii Volosheniuk", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.updateName(it) },
            label = { Text("Ваше ім'я", style = MaterialTheme.typography.bodySmall) }
        )
    }

}