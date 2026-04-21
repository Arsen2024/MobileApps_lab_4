package com.example.lab4_diary_app.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab4_diary_app.OnboardingContent

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OnboardingDarkPreview() {
    Lab4_diary_appTheme(darkTheme = true) {
        OnboardingContent(
            onEnterNameClick = {}
        )
    }
}