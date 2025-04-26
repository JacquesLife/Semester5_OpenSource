package com.example.budgettrackerapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.budgettrackerapp.ui.theme.AddExpense
import com.example.budgettrackerapp.ui.theme.navbar.BottomNavBar
import com.example.budgettrackerapp.ui.theme.splash.SplashScreen
import androidx.navigation.NavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Get current route to determine when to show bottom bar
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            // Only show the bottom bar on main screens (home, stats, wallet, profile)
            // Don't show it on splash or add_expense screens
            if (currentRoute != null && currentRoute != "splash" && !currentRoute.startsWith("add_expense")) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // Apply the padding to the NavHost content
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(navController)
                }
                composable("home") {
                    HomeScreen(navController)
                }
                composable("stats") {
                    StatsScreen(navController)
                }
                composable("wallet") {
                    WalletScreen(navController)
                }
                composable("profile") {
                    ProfileScreen(navController)
                }
                composable(
                    route = "add_expense?initialAmount={initialAmount}",
                    arguments = listOf(
                        navArgument("initialAmount") {
                            type = NavType.StringType
                            defaultValue = "0.00"
                            nullable = true
                        }
                    )
                ) { backStackEntry ->
                    val initialAmount = backStackEntry.arguments?.getString("initialAmount") ?: "0.00"
                    AddExpense(navController = navController)
                }
            }
        }
    }
}

// Placeholder screens - replace with your actual implementations or create them
@Composable
fun StatsScreen(navController: NavController) {
    // Your stats screen content
}

@Composable
fun WalletScreen(navController: NavController) {
    // Your wallet screen content
}

@Composable
fun ProfileScreen(navController: NavController) {
    // Your profile screen content
}

