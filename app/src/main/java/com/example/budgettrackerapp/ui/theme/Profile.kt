package com.example.budgettrackerapp.ui.theme.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgettrackerapp.data.BudgetViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: BudgetViewModel,
    username: String = "John Doe",
    rank: String = "Gold"
) {
    // Observe budget settings and expenses
    val budgetSettings by viewModel.budgetSettings.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    // Calculate total and remaining budget
    val totalBudget = budgetSettings?.monthlyBudget ?: 0.0
    val spent = expenses.sumOf { it.amount }
    val remainingBudget = totalBudget - spent

    // Trigger loading if needed
    LaunchedEffect(Unit) {
        viewModel.loadBudgetSettings()
        viewModel.loadExpenses()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Profile placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Photo", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = username,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Rank: $rank",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Total Budget: R %.2f".format(totalBudget),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Text(
            text = "Remaining: R %.2f".format(remainingBudget),
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF2E7D32) // dark green
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}
