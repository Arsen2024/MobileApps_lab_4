package com.example.lab4_diary_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab4_diary_app.ui.theme.Lab4_diary_appTheme
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lab4_diary_app.data.AppRepository
import com.example.lab4_diary_app.data.local.DiaryDatabase
import com.example.lab4_diary_app.data.preferences.UserPreferencesRepository
import com.example.lab4_diary_app.network.RetrofitInstance
import com.example.lab4_diary_app.routes.Screen
import com.example.lab4_diary_app.viewmodel.ListViewModel
import com.example.lab4_diary_app.viewmodel.ListViewModelFactory
import com.example.lab4_diary_app.viewmodel.SettingsViewModel
import com.example.lab4_diary_app.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4_diary_appTheme {
                val navController = rememberNavController()
                val database = DiaryDatabase.getDatabase(this)
                val repository = AppRepository(database.diaryDao(), RetrofitInstance.api)
                val preferences = UserPreferencesRepository(applicationContext)

                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(applicationContext)
                )

                val listViewModel: ListViewModel = viewModel(
                    factory = ListViewModelFactory(repository, preferences)
                )

                NavHost(
                    navController = navController,
                    startDestination = Screen.Onboarding.route
                ) {

                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(
                            navController = navController,
                            settingsViewModel = settingsViewModel
                        )
                    }

                    composable(Screen.EnterName.route) {
                        EnterNameScreen(
                            navController = navController,
                            settingsViewModel = settingsViewModel
                        )
                    }

                    composable(
                        route = Screen.Main.route,
                        arguments = listOf(
                            navArgument("userName") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        MainScreen(
                            settingsViewModel = settingsViewModel,
                            listViewModel = listViewModel,
                            navController = navController,
                            onOpenDetails = { id ->
                                navController.navigate("details/$id")
                            }
                        )
                    }

                    composable(
                        route = "details/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id") ?: ""

                        DetailScreen(
                            itemId = id,
                            repository = repository
                        )
                    }

                    composable(Screen.Add.route) {
                        AddScreen(
                            viewModel = listViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
            }
        }
    }
    }
}