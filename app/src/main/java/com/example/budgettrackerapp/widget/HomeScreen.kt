package com.example.budgettrackerapp.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgettrackerapp.data.BudgetSettings
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.User

@Composable
fun HomeScreen(viewModel: BudgetViewModel = viewModel(), navController: NavController, userId: Int) {
    val context = LocalContext.current

    var monthlyBudget by remember { mutableStateOf("") }
    var monthlyMaxGoal by remember { mutableStateOf("") }
    var monthlyMinGoal by remember { mutableStateOf("") }

    // Retrieve the user's information
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Set your budget goals
        Text("Set Your Budget Goals", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        // Input fields for monthly budget, max goal, and min goal
        OutlinedTextField(
            value = monthlyBudget,
            onValueChange = { monthlyBudget = it },
            label = { Text("Monthly Budget") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Input fields for monthly budget, max goal, and min goal
        OutlinedTextField(
            value = monthlyMaxGoal,
            onValueChange = { monthlyMaxGoal = it },
            label = { Text("Monthly Max Goal") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input fields for monthly budget, max goal, and min goal
        OutlinedTextField(
            value = monthlyMinGoal,
            onValueChange = { monthlyMinGoal = it },
            label = { Text("Monthly Min Goal") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Submit button to save the goals
        Button(
            onClick = {
                val settings = BudgetSettings(
                    monthlyBudget = monthlyBudget.toDoubleOrNull() ?: 0.0,
                    monthlyMaxGoal = monthlyMaxGoal.toDoubleOrNull() ?: 0.0,
                    monthlyMinGoal = monthlyMinGoal.toDoubleOrNull() ?: 0.0
                )
                viewModel.saveBudgetSettings(settings)

                // Navigate to the upcoming bills screen
                navController.navigate("upcoming_bills/$userId")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Submit", color = Color.White)
        }
    }
}