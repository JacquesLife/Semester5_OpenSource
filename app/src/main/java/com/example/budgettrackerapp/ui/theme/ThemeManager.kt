//Reference:https://www.youtube.com/watch?v=dlMByes6wDI
// This file is responsible fo handling the theme of the app
//it allows the user to toggle between light and dark mode

package com.example.budgettrackerapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.ColorScheme

object ThemeManager {
    private var isDarkMode = mutableStateOf(false)

    @Composable
    fun getCurrentTheme(): ColorScheme {
        return if (isDarkMode.value) {
            DarkColorScheme
        } else {
            LightColorScheme
        }
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    fun isDarkModeEnabled(): Boolean = isDarkMode.value
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------