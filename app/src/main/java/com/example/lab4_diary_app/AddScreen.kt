package com.example.lab4_diary_app

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.lab4_diary_app.viewmodel.ListViewModel
import com.example.lab4_diary_app.viewmodel.UiEvent
import androidx.compose.ui.text.input.ImeAction

@Composable
fun AddScreen(viewModel: ListViewModel, onBack: () -> Unit, windowSizeClass: WindowSizeClass) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Особисте") }
    var isFavorite by remember { mutableStateOf(false) }
    var mood by remember { mutableFloatStateOf(5f) }

    val categories = listOf("Особисте", "Робота", "Навчання", "Ідеї")
    var expanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var priorityError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var moodError by remember { mutableStateOf<String?>(null) }

    var titleFocused by remember { mutableStateOf(false) }
    var descFocused by remember { mutableStateOf(false) }
    var priorityFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val titleFocus = remember { FocusRequester() }
    val descriptionFocus = remember { FocusRequester() }
    val priorityFocus = remember { FocusRequester() }

    val scrollState = rememberScrollState()

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> {
                    onBack()
                }
                else -> {}
            }
        }
    }

    fun validate(): Boolean {
        var valid = true

        titleError = when {
            title.isBlank() -> "Заголовок не може бути пустим"
            title.length < 3 -> "Мінімум 3 символи"
            else -> null
        }
        if (titleError != null) valid = false

        descriptionError = if (description.isBlank())
            "Опис обов'язковий"
        else null
        if (descriptionError != null) valid = false

        priorityError = when {
            priority.isBlank() -> "Введіть число"
            priority.toIntOrNull() == null -> "Тільки числа"
            priority.toInt() !in 1..5 -> "Діапазон 1-5"
            else -> null
        }
        if (priorityError != null) valid = false

        categoryError = if (category.isBlank()) "Оберіть категорію" else null
        if (categoryError != null) valid = false

        moodError = if (mood.toInt() !in 1..10) "Діапазон 1-10" else null
        if (moodError != null) valid = false

        return valid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val isTablet = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

        Column(
            modifier = if(isTablet) {
                Modifier
                    .width(600.dp)
                    .verticalScroll(scrollState)
            } else {
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            }
        ) {
            Text("Додати запис", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок") },
                isError = titleError != null,
                supportingText = {
                    titleError?.let { Text(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocus)
                    .onFocusChanged {
                        if (titleFocused && !it.isFocused) validate()
                        titleFocused = it.isFocused
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { descriptionFocus.requestFocus() }
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Опис") },
                isError = descriptionError != null,
                supportingText = {
                    descriptionError?.let { Text(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocus)
                    .onFocusChanged {
                        if (descFocused && !it.isFocused) validate()
                        descFocused = it.isFocused
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { priorityFocus.requestFocus() }
                )
            )

            Spacer(Modifier.height(16.dp))

            Text("Основна інформація", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = priority,
                onValueChange = { priority = it.filter { ch -> ch.isDigit() } },
                label = { Text("Пріоритет (1-5)") },
                isError = priorityError != null,
                supportingText = {
                    priorityError?.let { Text(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(priorityFocus)
                    .onFocusChanged {
                        if (priorityFocused && !it.isFocused) validate()
                        priorityFocused = it.isFocused
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            )

            Spacer(Modifier.height(8.dp))

            Text("Категорія")

            Button(onClick = { expanded = true }) {
                Text(category.ifBlank { "Оберіть" })
            }

            categoryError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            category = it
                            categoryError = null
                            expanded = false
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Додаткові параметри", style = MaterialTheme.typography.titleMedium)

            Row {
                Text("Обране")
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = isFavorite,
                    onCheckedChange = { isFavorite = it }
                )
            }

            Spacer(Modifier.height(8.dp))

            Text("Настрій: ${mood.toInt()}")

            Slider(
                value = mood,
                onValueChange = {
                    mood = it
                    validate()
                },
                valueRange = 1f..10f
            )

            moodError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    if (validate()) {
                        isLoading = true
                        viewModel.addItem(
                            title = title,
                            description = description,
                            isFavorite = isFavorite,
                            priority = priority.toIntOrNull() ?: 0,
                            category = category,
                            mood = mood.toInt()
                        )
                    }
                },
                enabled =
                    titleError == null &&
                            descriptionError == null &&
                            priorityError == null &&
                            categoryError == null &&
                            moodError == null &&
                            title.isNotBlank() && description.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    Text("Збереження...")
                } else {
                    Text("Зберегти")
                }
            }
        }
    }
}