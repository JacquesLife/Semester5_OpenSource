/// Reference https://www.youtube.com/watch?v=LfHkAUzup5E
/// This is the user profile page where the user can view their profile information
/// such as their username, rank, total budget, remaining budget, and their expenses
/// They will also be able to logout and edit their monthly budget

package com.example.budgettrackerapp.ui.theme

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

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: BudgetViewModel,
    username: String = "John Doe",
    rank: String = "Gold"
) {
    val budgetSettings by viewModel.budgetSettings.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val totalBudget = budgetSettings?.monthlyBudget ?: 0.0
    val spent = expenses.sumOf { it.amount }
    val remainingBudget = (totalBudget - spent).coerceAtLeast(0.0)

    val user = viewModel.loginResult.collectAsState().value

    // Load data when the user is logged in
    LaunchedEffect(user) {
        if (user != null) {
            viewModel.loadBudgetSettings()
            viewModel.loadExpenses(user.userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
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
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Button for Edit Monthly Budget (Directs to Home Screen)
        Button(
            onClick = {
                navController.navigate("home/${user?.userId}")
                },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B8DEF))
        ) {
            Text("Edit Monthly Budget", color = Color.White)
        }

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------