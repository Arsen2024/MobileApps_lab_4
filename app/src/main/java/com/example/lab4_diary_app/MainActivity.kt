package com.example.lab4_diary_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lab4_diary_app.ui.theme.Lab4_diary_appTheme
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.routes.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4_diary_appTheme {
                val navController = rememberNavController()
                val repository = AppRepository()

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

                        val userName = backStackEntry.arguments?.getString("userName") ?: ""

                        MainScreen(
                            userName = userName,
                            onOpenDetails = { id ->
                                navController.navigate("details/$id")
                            }
                        )
                    }

                    composable(
                        route = "details/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0

                        DetailScreen(
                            itemId = id,
                            repository = repository
                        )
                    }
            }
        }
    }
    }
}