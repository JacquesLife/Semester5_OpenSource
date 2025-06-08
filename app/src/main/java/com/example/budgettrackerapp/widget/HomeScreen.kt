/// Reference: https://developer.android.com/develop/ui/compose/text/user-input
/// The home screen is quite basic it doesnt handle complicated logic just allows the user to set their budget
/// with minimum and max goals for monthly budget
/// However the values it provides are essential for the rest of the app

package com.example.budgettrackerapp.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgettrackerapp.data.BudgetSettings
import com.example.budgettrackerapp.data.BudgetViewModel

@Composable
fun HomeScreen(viewModel: BudgetViewModel = viewModel(), navController: NavController, userId: String) {
    LocalContext.current

    var monthlyBudget by remember { mutableStateOf("") }
    var monthlyMaxGoal by remember { mutableStateOf("") }
    var monthlyMinGoal by remember { mutableStateOf("") }

    // Retrieve the user's information
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Set your budget goals
        Text("Set Your Budget Goals", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        // Monthly Budget Input
        OutlinedTextField(
            value = monthlyBudget,
            onValueChange = { newValue ->
                // Only allow numbers and decimal points
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    monthlyBudget = newValue
                }
            },
            label = { Text("Monthly Budget", color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            singleLine = true,
            placeholder = { Text("Enter your monthly budget", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input fields for monthly budget, max goal, and min goal
        OutlinedTextField(
            value = monthlyMaxGoal,
            onValueChange = { newValue ->
                // Only allow numbers and decimal points
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    monthlyMaxGoal = newValue
                }
            },
            label = { Text("Monthly Max Goal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter maximum spending goal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input fields for monthly budget, max goal, and min goal
        OutlinedTextField(
            value = monthlyMinGoal,
            onValueChange = { newValue ->
                // Only allow numbers and decimal points
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    monthlyMinGoal = newValue
                }
            },
            label = { Text("Monthly Min Goal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter minimum spending goal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Validate inputs with safe fallbacks
                val validBudget = monthlyBudget.toDoubleOrNull()?.takeIf { it > 0.0 } ?: 0.0
                val validMaxGoal = monthlyMaxGoal.toDoubleOrNull()?.takeIf { it >= 0.0 } ?: 0.0
                val validMinGoal = monthlyMinGoal.toDoubleOrNull()?.takeIf { it >= 0.0 } ?: 0.0
                
                val settings = BudgetSettings(
                    userId = userId,
                    monthlyBudget = validBudget,
                    monthlyMaxGoal = validMaxGoal,
                    monthlyMinGoal = validMinGoal
                )
                
                viewModel.saveBudgetSettings(settings) { success ->
                    if (success) {
                        // Navigate to the upcoming bills screen
                        try {
                            navController.navigate("upcoming_bills/$userId")
                        } catch (e: Exception) {
                            // Fallback navigation
                            navController.popBackStack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Submit", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------