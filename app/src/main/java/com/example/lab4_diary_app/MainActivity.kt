package com.example.lab4_diary_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab4_diary_app.ui.theme.Lab4_diary_appTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.navigation.NavType
import kotlinx.coroutines.delay
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lab4_diary_app.Routes.Screen
import com.example.lab4_diary_app.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4_diary_appTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Onboarding.route
                ) {

                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(navController)
                    }

                    composable(Screen.EnterName.route) {
                        EnterNameScreen(navController)
                    }

                    composable(
                        route = Screen.Main.route,
                        arguments = listOf(
                            navArgument("userName") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val userName =
                            backStackEntry.arguments?.getString("userName") ?: ""

                        MainScreen(userName)
                    }
            }
        }
    }
    }
}