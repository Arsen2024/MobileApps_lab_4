package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab4_diary_app.viewmodel.SettingsViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel
) {
    val name by settingsViewModel.userName.collectAsStateWithLifecycle()

    val sortOrder by settingsViewModel.sortOrder.collectAsStateWithLifecycle()
    val onlyFavorites by settingsViewModel.showOnlyFavorites.collectAsStateWithLifecycle()

    var editableName by remember { mutableStateOf("") }

    LaunchedEffect(name) {
        editableName = name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Налаштування")

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = editableName,
            onValueChange = { editableName = it },
            label = { Text("Ваше ім'я", style = MaterialTheme.typography.bodySmall) }
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            settingsViewModel.saveName(editableName)
        }) {
            Text("Зберегти зміни")
        }

        Spacer(Modifier.height(32.dp))

        Text("Фільтр записів", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Тільки обрані")
            Spacer(Modifier.weight(1f))
            Switch(
                checked = onlyFavorites,
                onCheckedChange = {
                    settingsViewModel.setShowOnlyFavorites(it)
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("Сортування", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = sortOrder == SortOrder.NEWEST,
                onClick = { settingsViewModel.setSortOrder(SortOrder.NEWEST) }
            )
            Text("Спочатку нові")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = sortOrder == SortOrder.OLDEST,
                onClick = { settingsViewModel.setSortOrder(SortOrder.OLDEST) }
            )
            Text("Спочатку старі")
        }
    }

}