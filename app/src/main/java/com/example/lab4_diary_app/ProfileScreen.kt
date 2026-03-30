package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(userName: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Diary App")
        Text("Version 1.0")
        Text("Developer: Arsenii Volosheniuk")

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = onNameChange,
            label = { Text("Ваше ім'я") }
        )
    }

}