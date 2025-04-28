package com.example.budgettrackerapp.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.budgettrackerapp.data.BudgetViewModel

@Composable
fun HomeScreen(viewModel: BudgetViewModel, navController: NavHostController) {
    var yearlyBudgetInput by remember { mutableStateOf("") }
    var monthlyMaxInput by remember { mutableStateOf("") }
    var monthlyMinInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Set Your Budget Goals", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = yearlyBudgetInput,
            onValueChange = { yearlyBudgetInput = it },
            label = { Text("Yearly Budget") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = monthlyMaxInput,
            onValueChange = { monthlyMaxInput = it },
            label = { Text("Monthly Max Goal") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = monthlyMinInput,
            onValueChange = { monthlyMinInput = it },
            label = { Text("Monthly Min Goal") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val yearlyBudget = yearlyBudgetInput.toDoubleOrNull() ?: 0.0
                val monthlyMaxGoal = monthlyMaxInput.toDoubleOrNull() ?: 0.0
                val monthlyMinGoal = monthlyMinInput.toDoubleOrNull() ?: 0.0

                viewModel.setBudget(yearlyBudget, monthlyMaxGoal, monthlyMinGoal)

                navController.navigate("transaction")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}