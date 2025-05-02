package com.example.budgettrackerapp.ui.theme.rewards

// Import necessary Compose and AndroidX UI libraries
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment as UiAlignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.budgettrackerapp.R
import kotlin.math.min

// Main composable function for the Rewards screen
@Composable
fun RewardsScreen() {
    val userPoints = 230 // Dummy value for user points (replace with real user data later)

    // Get the user's current tier (e.g., Bronze, Silver, etc.) based on points
    val tier = getUserTier(userPoints)

    // Get the point range for the current tier
    val (minPoints, maxPoints) = getTierRange(tier)

    // Calculate the progress towards the next tier as a fraction (0.0 to 1.0)
    val progressFraction = calculateProgress(userPoints, minPoints, maxPoints)

    // Layout for the rewards screen using a vertical column
    //https://medium.com/@rowaido.game/mastering-layout-basics-in-jetpack-compose-8f85853855e3
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
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // If user is not in the highest tier, show progress bar
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

        Spacer(modifier = Modifier.height(32.dp))

        // Sub-header for tier list
        Text(
            text = "Reward Tiers",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show cards for each reward tier with icon and point range
        //https://medium.com/@acceldia/jetpack-compose-creating-expandable-cards-with-content-9ea1eae09efe
        TierCard("Bronze", 0, 100, R.drawable.bronze)
        TierCard("Silver", 101, 250, R.drawable.silver)
        TierCard("Gold", 251, 500, R.drawable.gold)
        TierCard("Platinum", 501, Int.MAX_VALUE, R.drawable.platinum)
    }
}

// A reusable composable that displays a tier card with name, icon, and point range
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
