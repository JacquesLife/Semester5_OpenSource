package com.example.budgettrackerapp.ui.theme.rewards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment as UiAlignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun RewardsScreen() {
    val userPoints = 230 // Dummy points for now
    val tier = getUserTier(userPoints)

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

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Reward Tiers",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        TierCard("Bronze", 0, 100)
        TierCard("Silver", 101, 250)
        TierCard("Gold", 251, 500)
        TierCard("Platinum", 501, Int.MAX_VALUE)
    }
}

@Composable
fun TierCard(tierName: String, minPoints: Int, maxPoints: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = UiAlignment.Start
        ) {
            Text(text = tierName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Points: $minPoints - ${if (maxPoints == Int.MAX_VALUE) "∞" else maxPoints}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
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
