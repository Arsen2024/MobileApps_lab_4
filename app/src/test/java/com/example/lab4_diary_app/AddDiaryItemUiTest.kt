package com.example.lab4_diary_app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test


class AddDiaryItemUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addDiaryItemFlow() {

        // 1. Відкрити форму додавання
        composeTestRule
            .onNodeWithTag("addButton")
            .performClick()

        // 2. Ввести title
        composeTestRule
            .onNodeWithTag("titleField")
            .performTextInput("UI Test Title")

        // 3. Ввести description
        composeTestRule
            .onNodeWithTag("descriptionField")
            .performTextInput("UI Test Description")

        // 4. Натиснути save
        composeTestRule
            .onNodeWithTag("saveButton")
            .performClick()

        // 5. Перевірити що новий item є у списку
        composeTestRule
            .onNodeWithText("UI Test Title")
            .assertIsDisplayed()
    }
}