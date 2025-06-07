package com.example.budgettrackerapp.ui.theme.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.ui.theme.ThemeManager

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: BudgetViewModel,
    userId: String
) {
    var isDarkMode by remember { mutableStateOf(ThemeManager.isDarkModeEnabled()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Dark Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isDarkMode,
                onCheckedChange = { newValue ->
                    isDarkMode = newValue
                    ThemeManager.toggleDarkMode()
                }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Add more settings options here as needed
    }
}