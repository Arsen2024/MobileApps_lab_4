package com.example.lab4_diary_app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

data class DiaryItem(val title: String, val description: String)

@Composable
fun MainScreen(userName: String) {
    var selectedTab by remember { mutableStateOf(MainTab.LIST) }
    val navController = rememberNavController()
    var userName by remember { mutableStateOf(userName) }

    val items = remember {
        listOf(
            DiaryItem("Запис 1", "Опис 1"),
            DiaryItem("Запис 2", "Опис 2"),
            DiaryItem("Запис 3", "Опис 3"),
            DiaryItem("Запис 4", "Опис 4"),
            DiaryItem("Запис 5", "Опис 5"),
            DiaryItem("Запис 6", "Опис 6")
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar{
                NavigationBarItem(
                    selected = selectedTab == MainTab.LIST,
                    onClick = { selectedTab = MainTab.LIST },
                    label = { Text(MainTab.LIST.title) },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.GRID,
                    onClick = { selectedTab = MainTab.GRID },
                    label = { Text(MainTab.GRID.title) },
                    icon = { Icon(Icons.Default.GridOn, null) }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.PROFILE,
                    onClick = { selectedTab = MainTab.PROFILE },
                    label = { Text(MainTab.PROFILE.title) },
                    icon = { Icon(Icons.Default.Person, null) }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController =  navController,
            startDestination = "tabs",
            modifier = Modifier.padding(padding)
        ) {
            composable("tabs") {
                when(selectedTab) {
                    MainTab.LIST -> ListScreen(items) { item ->
                        navController.navigate("details/${item.title}/${item.description}")
                    }
                    MainTab.GRID -> GridScreen(items) { item ->
                        navController.navigate("details/${item.title}/${item.description}")
                    }
                    MainTab.PROFILE -> ProfileScreen(userName = userName, onNameChange = { userName = it })
                }
            }

            composable(
                route = "details/{title}/{description}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("description") { type = NavType.StringType }
                )
                ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val description = backStackEntry.arguments?.getString("description") ?: ""

                DetailScreen(title, description)
            }
        }
    }
}