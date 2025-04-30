package com.example.budgettrackerapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.ui.theme.AddExpense
import com.example.budgettrackerapp.ui.theme.navbar.BottomNavBar
import com.example.budgettrackerapp.ui.theme.rewards.RewardsScreen
import com.example.budgettrackerapp.ui.theme.splash.SplashScreen
import com.example.budgettrackerapp.ui.theme.stats.StatsScreen
import com.example.budgettrackerapp.widget.HomeScreen
import com.example.budgettrackerapp.widget.LoginScreen
import com.example.budgettrackerapp.widget.TransactionScreen
import com.example.budgettrackerapp.widget.UpcomingBillsScreen

@Composable
fun AppNavigation(viewModel: BudgetViewModel) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute != "splash" && !currentRoute.startsWith("add_expense")) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
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
                composable("upcoming_bills") {
                    UpcomingBillsScreen(navController = navController, viewModel = viewModel)
                }
                composable("stats") {
                    StatsScreen(navController)
                }
                composable("wallet") {
                    RewardsScreen() // No arguments are needed here
                }

                composable("profile") {
                    ProfileScreen(navController)
                }
                composable("login") {
                    LoginScreen(
                        viewModel = viewModel,
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
                    AddExpense(
                        navController = navController,
                        initialAmount = initialAmount
                    )
                }
            }
        }
    }
}

// Placeholder screens - you can replace these later if needed

@Composable
fun ProfileScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen")
    }
}