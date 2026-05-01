package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lab4_diary_app.routes.Screen
import com.example.lab4_diary_app.viewmodel.SettingsViewModel

@Composable
fun EnterNameScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Введіть ваше ім'я")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ім'я") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            settingsViewModel.saveName(name)
            navController.navigate(Screen.Main.createRoute(name)) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }, enabled = name.isNotBlank()
        ) {
            Text("Зберегти")
        }
    }
}