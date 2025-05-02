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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import kotlin.math.min

@Composable
fun RewardsScreen(viewModel: BudgetViewModel = viewModel()) {
    val userPoints by remember {
        viewModel.rewardPoints
    }.collectAsState()
    val tier = getUserTier(userPoints)
    val (minPoints, maxPoints) = getTierRange(tier)
    val progressFraction = calculateProgress(userPoints, minPoints, maxPoints)


    // Get the current user ID if logged in
    val userId = viewModel.loginResult.collectAsState().value?.userId

    // Call loadExpenses when userId is available
    LaunchedEffect(userId) {
        if (userId != null) {
            println("DEBUG: Loading data for user ID: $userId")
            viewModel.loadExpenses(userId)
            viewModel.loadBudgetSettings(userId)

            // Force recalculation of rewards
            viewModel.calculateSavingsAndRewards(userId)
        } else {
            println("DEBUG: User ID is null, cannot load data")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = UiAlignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "My Rewards",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Current Points: $userPoints",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tier: $tier",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (tier != "Platinum") {
            LinearProgressIndicator(
                progress = progressFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.LightGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Progress to next tier: ${userPoints - minPoints}/${maxPoints - minPoints} pts",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        } else {
            Text(
                text = "You've reached the highest tier!",
                fontSize = 14.sp,
                color = Color(0xFF388E3C)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Reward Tiers",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        TierCard("Bronze", 0, 100, R.drawable.bronze)
        TierCard("Silver", 101, 250, R.drawable.silver)
        TierCard("Gold", 251, 500, R.drawable.gold)
        TierCard("Platinum", 501, Int.MAX_VALUE, R.drawable.platinum)
    }
}



@Composable
fun TierCard(tierName: String, minPoints: Int, maxPoints: Int, iconRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = UiAlignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$tierName Icon",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            Column {
                Text(text = tierName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Points: $minPoints - ${if (maxPoints == Int.MAX_VALUE) "∞" else maxPoints}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

fun getUserTier(points: Int): String {
    return when {
        points <= 100 -> "Bronze"
        points <= 250 -> "Silver"
        points <= 500 -> "Gold"
        else -> "Platinum"
    }
}

fun getTierRange(tier: String): Pair<Int, Int> {
    return when (tier) {
        "Bronze" -> 0 to 100
        "Silver" -> 101 to 250
        "Gold" -> 251 to 500
        else -> 501 to Int.MAX_VALUE
    }
}

fun calculateProgress(points: Int, minPoints: Int, maxPoints: Int): Float {
    return if (maxPoints == Int.MAX_VALUE) 1f
    else min((points - minPoints).toFloat() / (maxPoints - minPoints), 1f)
}
