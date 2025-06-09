/// References: https://medium.com/@rowaido.game/mastering-layout-basics-in-jetpack-compose-8f85853855e3
/// This is our functional rewards screen that calculates user points based on spending goals
/// it will display the users rank depending on how well they have achieved their monthly goals
/// Tiers include bronze, silver, gold, and platinum

package com.example.budgettrackerapp.ui.theme.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import kotlin.math.min

// Data class for reward tier information
data class RewardTier(
    val name: String,
    val minPoints: Int,
    val maxPoints: Int,
    val iconRes: Int,
    val color: Color
)

// Object containing reward tier constants
object RewardTiers {
    val BRONZE = RewardTier("Bronze", 0, 100, R.drawable.bronze, Color(0xFFCD7F32))
    val SILVER = RewardTier("Silver", 101, 250, R.drawable.silver, Color(0xFFC0C0C0))
    val GOLD = RewardTier("Gold", 251, 500, R.drawable.gold, Color(0xFFFFD700))
    val PLATINUM = RewardTier("Platinum", 501, Int.MAX_VALUE, R.drawable.platinum, Color(0xFFE5E4E2))

    val ALL_TIERS = listOf(BRONZE, SILVER, GOLD, PLATINUM)
}

@Composable
fun RewardsScreen(
    viewModel: BudgetViewModel
) {
    val budgetSettings by viewModel.budgetSettings.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val user by viewModel.loginResult.collectAsState()

    // Load data when the user is logged in
    LaunchedEffect(user) {
        user?.let { currentUser ->
            viewModel.loadBudgetSettings(currentUser.userId)
            viewModel.loadExpenses(currentUser.userId)
        }
    }

    // Calculate total spent this month
    val totalSpent = expenses.sumOf { it.amount }

    // Get budget settings
    val minGoal = budgetSettings?.monthlyMinGoal ?: 0.0
    val maxGoal = budgetSettings?.monthlyMaxGoal ?: 0.0
    budgetSettings?.monthlyBudget ?: 0.0

    // Calculate user points and tier
    val userPoints = calculateUserPoints(totalSpent, minGoal, maxGoal)
    val currentTier = getUserTier(userPoints)
    val (minPoints, maxPoints) = getTierRange(currentTier)
    val progressFraction = calculateProgress(userPoints, minPoints, maxPoints)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "My Rewards",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Spending Summary Card
        SpendingSummaryCard(
            totalSpent = totalSpent,
            minGoal = minGoal,
            maxGoal = maxGoal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Points Display
        Text(
            text = "Current Points: $userPoints",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Current Tier Display
        Text(
            text = "Tier: ${currentTier.name}",
            style = MaterialTheme.typography.titleMedium,
            color = currentTier.color
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Section
        if (currentTier != RewardTiers.PLATINUM) {
            ProgressSection(
                progressFraction = progressFraction,
                currentTier = currentTier,
                userPoints = userPoints,
                minPoints = minPoints,
                maxPoints = maxPoints
            )
        } else {
            Text(
                text = "You've reached the highest tier!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Points Explanation Card
        PointsExplanationCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Tier List
        Text(
            text = "Reward Tiers",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display all tier cards
        RewardTiers.ALL_TIERS.forEach { tier ->
            TierCard(
                tier = tier,
                currentPoints = userPoints
            )
        }
    }
}

@Composable
private fun SpendingSummaryCard(
    totalSpent: Double,
    minGoal: Double,
    maxGoal: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Min Goal: R %.2f | Max Goal: R %.2f".format(minGoal, maxGoal),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProgressSection(
    progressFraction: Float,
    currentTier: RewardTier,
    userPoints: Int,
    minPoints: Int,
    maxPoints: Int
) {
    LinearProgressIndicator(
        progress = { progressFraction },
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp),
        color = currentTier.color,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Progress to next tier: ${userPoints - minPoints}/${maxPoints - minPoints} pts",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun PointsExplanationCard() {
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
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "• Spend more than maximum goal: 0 points",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "• Closer to minimum goal = more points",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "• Maximum 100 points per month",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun TierCard(tier: RewardTier, currentPoints: Int) {
    val isCurrentTier = currentPoints >= tier.minPoints && (tier.maxPoints == Int.MAX_VALUE || currentPoints <= tier.maxPoints)
    val cardColor = if (isCurrentTier) tier.color.copy(alpha = 0.1f) else Color.Transparent

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = tier.iconRes),
                contentDescription = "${tier.name} Icon",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tier.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isCurrentTier) tier.color else MaterialTheme.colorScheme.onBackground
                    )
                    if (isCurrentTier) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CURRENT",
                            fontSize = 10.sp,
                            color = tier.color,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Text(
                    text = "Points: ${tier.minPoints} - ${if (tier.maxPoints == Int.MAX_VALUE) "∞" else tier.maxPoints}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Business Logic Functions
private fun calculateUserPoints(totalSpent: Double, minGoal: Double, maxGoal: Double): Int {
    if (minGoal <= 0 || maxGoal <= 0) return 0
    if (totalSpent < minGoal || totalSpent > maxGoal) return 0

    val range = maxGoal - minGoal
    if (range <= 0) return 0

    val distanceFromMin = totalSpent - minGoal
    val pointsRatio = 1.0 - (distanceFromMin / range)

    return (pointsRatio * 100).toInt().coerceIn(0, 100)
}

private fun getUserTier(points: Int): RewardTier {
    return RewardTiers.ALL_TIERS.find { tier ->
        points >= tier.minPoints && (tier.maxPoints == Int.MAX_VALUE || points <= tier.maxPoints)
    } ?: RewardTiers.BRONZE
}

private fun getTierRange(tier: RewardTier): Pair<Int, Int> {
    return tier.minPoints to tier.maxPoints
}

private fun calculateProgress(points: Int, minPoints: Int, maxPoints: Int): Float {
    return if (maxPoints == Int.MAX_VALUE) 1f
    else min((points - minPoints).toFloat() / (maxPoints - minPoints), 1f)
}


//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------

