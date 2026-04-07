package com.example.lab4_diary_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lab4_diary_app.routes.Screen

@Composable
fun OnboardingScreen(navController: NavController) {
    var userName by remember { mutableStateOf("") }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val nameFromBack by savedStateHandle
        ?.getStateFlow("userName", "")
        ?.collectAsState() ?: remember { mutableStateOf("") }

    if(nameFromBack.isNotEmpty()) {
        userName = nameFromBack
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Diary App", fontSize = 26.sp)

        Spacer(Modifier.height(32.dp))

        Button(onClick = {
            navController.navigate(Screen.EnterName.route)
        }) {
            Text("Ввести ім'я")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Screen.Main.createRoute(userName)) {
                popUpTo(Screen.Onboarding.route){
                    inclusive = true
                }
            }
        }, enabled = userName.isNotBlank()
        ) {
            if(userName.isBlank()) {
                Text("Розпочати")
            } else {
                Text("Привіт, $userName! Розпочати")
            }
        }
    }

}