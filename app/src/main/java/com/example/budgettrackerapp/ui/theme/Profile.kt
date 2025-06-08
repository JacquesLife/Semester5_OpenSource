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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.ui.theme.rewards.RewardTier
import com.example.budgettrackerapp.ui.theme.rewards.RewardTiers

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: BudgetViewModel,
    username: String
) {
    val user by viewModel.loginResult.collectAsState()
    val budgetSettings by viewModel.budgetSettings.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    // Calculate user points and tier
    val totalSpent = expenses.sumOf { it.amount }
    val minGoal = budgetSettings?.monthlyMinGoal ?: 0.0
    val maxGoal = budgetSettings?.monthlyMaxGoal ?: 0.0
    val userPoints = calculateUserPoints(totalSpent, minGoal, maxGoal)
    val currentTier = getUserTier(userPoints)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile picture with first letter
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.take(1).uppercase(),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username
        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rank with tier color
        Text(
            text = "Rank: ${currentTier.name}",
            fontSize = 16.sp,
            color = currentTier.color
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Monthly Budget
        Text(
            text = "Monthly Budget",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "R${budgetSettings?.monthlyBudget ?: 0.0}",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Button(
            onClick = {
                navController.navigate("home/${user?.userId}")
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Edit Monthly Budget", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout", color = MaterialTheme.colorScheme.onError)
        }
    }
}

// Helper functions from RewardsScreen
private fun calculateUserPoints(totalSpent: Double, minGoal: Double, maxGoal: Double): Int {
    if (totalSpent < minGoal || totalSpent > maxGoal) return 0
    val range = maxGoal - minGoal
    val distanceFromMin = totalSpent - minGoal
    return ((1 - (distanceFromMin / range)) * 500).toInt()
}

private fun getUserTier(points: Int): RewardTier {
    return when {
        points >= 501 -> RewardTiers.PLATINUM
        points >= 251 -> RewardTiers.GOLD
        points >= 101 -> RewardTiers.SILVER
        else -> RewardTiers.BRONZE
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------