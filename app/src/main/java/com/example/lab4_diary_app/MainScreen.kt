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

@Composable
fun MainScreen(userName: String, onOpenDetails: (Int) -> Unit) {
    var selectedTab by remember { mutableStateOf(MainTab.LIST) }

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

        when (selectedTab) {
            MainTab.LIST -> ListScreen(
                onItemClick = { item -> onOpenDetails(item.id) },
                modifier = Modifier.padding(padding)
            )
            MainTab.GRID -> GridScreen(
                onItemClick = { item -> onOpenDetails(item.id) },
                modifier = Modifier.padding(padding)
            )
            MainTab.PROFILE -> ProfileScreen(modifier = Modifier.padding(padding), initialName = userName)
        }
    }
}