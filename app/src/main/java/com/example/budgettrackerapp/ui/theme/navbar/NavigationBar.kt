/// References:  https://www.youtube.com/watch?v=LfHkAUzup5E
/// This is the navigation bar it contains the bottom navigation bar visuals such as svgs and the FAB
/// While the navigation.kt file handles the logic this file handles the visuals

package com.example.budgettrackerapp.ui.theme.navbar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.budgettrackerapp.R

data class BottomNavItem(
    val title: String,
    val iconResId: Int,
    val route: String
)

@Composable
fun BottomNavBar(navController: NavController, userId: String) {

    // List of bottom navigation items
    val items = listOf(
        BottomNavItem("Home", R.drawable.home, "upcoming_bills/$userId"),
        BottomNavItem("stats", R.drawable.chart, "stats/$userId"),
        BottomNavItem("Wallet", R.drawable.wallet, "wallet/$userId"),
        BottomNavItem("profile", R.drawable.user, "profile/$userId")
    )

    // Bottom navigation bar
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            // Get the current route to highlight the selected item
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            // Loop through the items and create a NavigationBarItem for each
            items.forEachIndexed { index, item ->
                if (index == 2) {
                    Spacer(modifier = Modifier.weight(1f)) // Space for FAB
                }

                // Create a NavigationBarItem for each item
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconResId),
                            contentDescription = item.title,
                            tint = if (currentRoute == item.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    label = null,
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )

                // Add a spacer for the FAB
                if (index == 1) {
                    Spacer(modifier = Modifier.weight(1f)) // Space for FAB
                }
            }
        }

        // Floating Action Button (FAB)
        FloatingActionButton(
            onClick = {
                navController.navigate("add_expense?initialAmount=0.00&userId=$userId") {
                    launchSingleTop = true
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .size(56.dp)
                .shadow(8.dp, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Expense",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

//----------------------------------------------------End_of_File-----------------------------------------------------------------------------------------