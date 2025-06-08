/// References: https://medium.com/@rowaido.game/mastering-layout-basics-in-jetpack-compose-8f85853855e3
/// This is our functional rewards screen that calculates user points based on spending goals
/// it will display the users rank depending on how well they have achieved their monthly goals
/// Tiers include bronze, silver, gold, and platinum

package com.example.budgettrackerapp.ui.theme.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment as UiAlignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import kotlin.math.min
import kotlin.math.max

@Composable
fun RewardsScreen(
    navController: NavController,
    viewModel: BudgetViewModel,
    userId: String
) {
    val budgetSettings by viewModel.budgetSettings.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val user by viewModel.loginResult.collectAsState()

    // Load data when the user is logged in
    LaunchedEffect(user) {
        // Solution 1: Use local variable to avoid smart cast issue
        val currentUser = user
        if (currentUser != null) {
            viewModel.loadBudgetSettings(currentUser.userId)
            viewModel.loadExpenses(currentUser.userId)
        }
    }

    // Alternative Solution 2: Use user?.let { } block
    // LaunchedEffect(user) {
    //     user?.let { currentUser ->
    //         viewModel.loadBudgetSettings(currentUser.userId)
    //         viewModel.loadExpenses(currentUser.userId)
    //     }
    // }

    // Calculate total spent this month
    val totalSpent = expenses.sumOf { it.amount }

    // Get budget settings
    val minGoal = budgetSettings?.monthlyMinGoal ?: 0.0
    val maxGoal = budgetSettings?.monthlyMaxGoal ?: 0.0
    val monthlyBudget = budgetSettings?.monthlyBudget ?: 0.0

    // Calculate user points based on spending vs goals
    val userPoints = calculateUserPoints(totalSpent, minGoal, maxGoal, monthlyBudget)

    // Get the user's current tier (e.g., Bronze, Silver, etc.) based on points
    val tier = getUserTier(userPoints)

    // Get the point range for the current tier
    val (minPoints, maxPoints) = getTierRange(tier)

    // Calculate the progress towards the next tier as a fraction (0.0 to 1.0)
    val progressFraction = calculateProgress(userPoints, minPoints, maxPoints)

    // Layout for the rewards screen using a vertical column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = UiAlignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header text
        Text(
            text = "My Rewards",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp)) // Vertical spacing

        // Display spending summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = UiAlignment.CenterHorizontally
            ) {
                Text(
                    text = "This Month's Spending",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total Spent: R %.2f".format(totalSpent),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = "Min Goal: R %.2f | Max Goal: R %.2f".format(minGoal, maxGoal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display current user points
        Text(
            text = "Current Points: $userPoints",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display current tier
        Text(
            text = "Tier: $tier",
            style = MaterialTheme.typography.titleMedium,
            color = getTierColor(tier)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // If user is not in the highest tier, show progress bar
        if (tier != "Platinum") {
            LinearProgressIndicator(
                progress = progressFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = getTierColor(tier),
                trackColor = Color.LightGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display numeric progress to next tier
            Text(
                text = "Progress to next tier: ${userPoints - minPoints}/${maxPoints - minPoints} pts",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        } else {
            // If already in Platinum tier
            Text(
                text = "You've reached the highest tier!",
                fontSize = 14.sp,
                color = Color(0xFF388E3C) // A shade of green
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Points explanation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "How Points Work:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Spend less than minimum goal: 0 points",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "• Spend more than maximum goal: 0 points",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "• Closer to minimum goal = more points",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "• Maximum 100 points per month",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sub-header for tier list
        Text(
            text = "Reward Tiers",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show cards for each reward tier with icon and point range
        TierCard("Bronze", 0, 100, R.drawable.bronze, userPoints)
        TierCard("Silver", 101, 250, R.drawable.silver, userPoints)
        TierCard("Gold", 251, 500, R.drawable.gold, userPoints)
        TierCard("Platinum", 501, Int.MAX_VALUE, R.drawable.platinum, userPoints)
    }
}

// A reusable composable that displays a tier card with name, icon, and point range
@Composable
fun TierCard(tierName: String, minPoints: Int, maxPoints: Int, iconRes: Int, currentPoints: Int) {
    val isCurrentTier = currentPoints >= minPoints && (maxPoints == Int.MAX_VALUE || currentPoints <= maxPoints)
    val cardColor = if (isCurrentTier) getTierColor(tierName).copy(alpha = 0.1f) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(if (isCurrentTier) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = UiAlignment.CenterVertically
        ) {
            // Tier icon
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$tierName Icon",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            // Tier name and point range text
            Column {
                Row(
                    verticalAlignment = UiAlignment.CenterVertically
                ) {
                    Text(
                        text = tierName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isCurrentTier) getTierColor(tierName) else Color.Black
                    )
                    if (isCurrentTier) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CURRENT",
                            fontSize = 10.sp,
                            color = getTierColor(tierName),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Text(
                    text = "Points: $minPoints - ${if (maxPoints == Int.MAX_VALUE) "∞" else maxPoints}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// Calculates user points based on spending vs goals
fun calculateUserPoints(totalSpent: Double, minGoal: Double, maxGoal: Double, monthlyBudget: Double): Int {
    // If no goals are set, return 0 points
    if (minGoal <= 0 || maxGoal <= 0) return 0

    // If spending is outside the acceptable range, return 0 points
    if (totalSpent < minGoal || totalSpent > maxGoal) return 0

    // Calculate points based on how close to minimum goal (closer = more points)
    // Maximum 100 points for spending exactly at minimum goal
    val range = maxGoal - minGoal
    if (range <= 0) return 0

    val distanceFromMin = totalSpent - minGoal
    val pointsRatio = 1.0 - (distanceFromMin / range)

    return (pointsRatio * 100).toInt().coerceIn(0, 100)
}

// Determines the user's tier based on their points
fun getUserTier(points: Int): String {
    return when {
        points <= 100 -> "Bronze"
        points <= 250 -> "Silver"
        points <= 500 -> "Gold"
        else -> "Platinum"
    }
}

// Returns the min and max point range for a given tier
fun getTierRange(tier: String): Pair<Int, Int> {
    return when (tier) {
        "Bronze" -> 0 to 100
        "Silver" -> 101 to 250
        "Gold" -> 251 to 500
        else -> 501 to Int.MAX_VALUE // Platinum tier has no upper limit
    }
}

// Calculates progress to next tier as a float (0.0 to 1.0)
fun calculateProgress(points: Int, minPoints: Int, maxPoints: Int): Float {
    return if (maxPoints == Int.MAX_VALUE) 1f // Already at top tier
    else min((points - minPoints).toFloat() / (maxPoints - minPoints), 1f)
}

// Returns the color associated with each tier
fun getTierColor(tier: String): Color {
    return when (tier) {
        "Bronze" -> Color(0xFFCD7F32)
        "Silver" -> Color(0xFFC0C0C0)
        "Gold" -> Color(0xFFFFD700)
        "Platinum" -> Color(0xFFE5E4E2)
        else -> Color.Gray
    }
}