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
import com.example.lab4_diary_app.model.DiaryEntry
import com.example.lab4_diary_app.ui.theme.Lab4_diary_appTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import com.example.lab4_diary_app.types.FilterType
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4_diary_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiaryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab4_diary_appTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDiaryScreen() {
    DiaryScreen()
}

@Composable
fun DiaryScreen(modifier: Modifier = Modifier) {
    val diaryEntries = remember { mutableStateListOf<DiaryEntry>() }

    var inputText by remember { mutableStateOf("") }

    var filterType by remember { mutableStateOf(FilterType.ALL) }

    val filteredEntries by remember(diaryEntries, filterType) {
        derivedStateOf {
            when(filterType) {
                FilterType.ALL -> diaryEntries
                FilterType.SHORT -> diaryEntries.filter { it.text.length <= 20 }
                FilterType.LONG -> diaryEntries.filter { it.text.length > 20 }
            }
        }
    }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)

        diaryEntries.addAll(
            listOf(
                DiaryEntry("Перший запис"),
                DiaryEntry("Сьогодні гарний день"),
                DiaryEntry("Другий запис"),
                DiaryEntry("Пишу лабораторну"),
                DiaryEntry("Ще один запис")
            )
        )
        isLoading = false
    }

    if(isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Завантаження...")
        }
    } else {
        Column(modifier = Modifier.padding(28.dp)) {
            Text(
                text = "Кількість записів: ${diaryEntries.size}"
            )
            if (diaryEntries.size > 5) {
                Text(
                    text = "Забагато записів!",
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            InputPanel(
                text = inputText,
                onTextChange = { inputText = it },
                onAddClick = {
                    if (inputText.isNotBlank()) {
                        diaryEntries.add(DiaryEntry(inputText))
                        inputText = ""
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { filterType = FilterType.ALL }) {
                    Text("Всі")
                }
                Button(onClick = { filterType = FilterType.SHORT }) {
                    Text("Короткі")
                }
                Button(onClick = { filterType = FilterType.LONG }) {
                    Text("Довгі")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (diaryEntries.isEmpty()) {
                Text("Список порожній. Додайте перший елемент.")
            } else if (filteredEntries.isEmpty()) {
                Text("Немає елементів для цього фільтра")
            } else {
                LazyColumn {
                    items(filteredEntries) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(entry.text)

                            Button(
                                onClick = {
                                    diaryEntries.remove(entry)
                                }
                            ) {
                                Text("Видалити")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputPanel(text: String, onTextChange: (String) -> Unit, onAddClick: () -> Unit) {
    Column {
        TextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Введіть запис...")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Додати")
        }
    }
}