package com.example.budgettrackerapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.widget.HomeScreen
import com.example.budgettrackerapp.widget.LoginScreen
import com.example.budgettrackerapp.widget.TransactionScreen
import androidx.lifecycle.viewmodel.compose.viewModel



@Composable
fun AppNavigation(viewModel: BudgetViewModel) {
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
                    HomeScreen(viewModel = viewModel, navController = navController)
                }
                composable("transaction") {
                    TransactionScreen(navController)
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
                composable("login") {
                    val vm: BudgetViewModel = viewModel()
                    LoginScreen(
                        viewModel      = vm,
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Stats Screen")
    }
}

@Composable
fun WalletScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Wallet Screen")
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen")
    }
}


