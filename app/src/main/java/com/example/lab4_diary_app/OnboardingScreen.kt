package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lab4_diary_app.routes.Screen
import com.example.lab4_diary_app.viewmodel.SettingsViewModel

@Composable
fun OnboardingScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val name by settingsViewModel.userName.collectAsStateWithLifecycle()

    LaunchedEffect(name) {
        if (name.isNotBlank()) {
            navController.navigate(Screen.Main.createRoute(name)) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }

    OnboardingContent(
        onEnterNameClick = {
            navController.navigate(Screen.EnterName.route)
        }
    )
}

@Composable
fun OnboardingContent(onEnterNameClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Diary App", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        Button(onClick = onEnterNameClick) {
            Text("Ввести ім'я", style = MaterialTheme.typography.titleMedium)
        }
    }
}