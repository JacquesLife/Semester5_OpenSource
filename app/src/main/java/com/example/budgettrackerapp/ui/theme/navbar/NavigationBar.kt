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
import androidx.compose.ui.graphics.Color
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
fun BottomNavBar(navController: NavController, userId: Int) {

    val items = listOf(
        BottomNavItem("Home", R.drawable.home, "home/$userId"),
        BottomNavItem("stats", R.drawable.chart, "stats/$userId"),
        BottomNavItem("Wallet", R.drawable.wallet, "wallet"),
        BottomNavItem("profile", R.drawable.user, "profile/$userId")
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            items.forEachIndexed { index, item ->
                if (index == 2) {
                    Spacer(modifier = Modifier.weight(1f)) // Space for FAB
                }

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconResId),
                            contentDescription = item.title,
                            tint = if (currentRoute == item.route) Color(0xFF5B8DEF) else Color.Gray
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
                        selectedIconColor = Color(0xFF5B8DEF),
                        indicatorColor = Color.White,
                        unselectedIconColor = Color.Gray
                    )
                )

                if (index == 1) {
                    Spacer(modifier = Modifier.weight(1f)) // Space for FAB
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate("add_expense?initialAmount=0.00&userId=$userId") {
                    launchSingleTop = true
                }
            },
            containerColor = Color(0xFF5B8DEF),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(56.dp)
                .shadow(8.dp, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Expense",
                tint = Color.White
            )
        }
    }
}
