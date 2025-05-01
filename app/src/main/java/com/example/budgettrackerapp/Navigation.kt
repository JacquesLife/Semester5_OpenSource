package com.example.budgettrackerapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.ui.theme.AddExpense
import com.example.budgettrackerapp.ui.theme.navbar.BottomNavBar
import com.example.budgettrackerapp.ui.theme.profile.ProfileScreen
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

    // Get the current username (null-safe)
    val loggedInUser = viewModel.loginResult.collectAsState().value
    val currentUsername = loggedInUser?.username ?: "User"

    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute != "splash" && !currentRoute.startsWith("add_expense")) {
                BottomNavBar(navController = navController, userId = loggedInUser?.userId ?: 0)
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
                composable("home/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                    HomeScreen(navController = navController, viewModel = viewModel, userId = userId)
                }
                composable("transaction/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                    TransactionScreen(navController = navController, viewModel = viewModel, userId = userId)
                }
                composable("upcoming_bills/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                    UpcomingBillsScreen(navController = navController, viewModel = viewModel, userId = userId)
                }

                composable("stats/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                    StatsScreen(navController = navController, viewModel = viewModel, userId = userId)
                }

                composable("wallet") {
                    RewardsScreen()
                }
                composable("profile/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                    val loggedInUser = viewModel.loginResult.value
                    val username = loggedInUser?.username ?: "User"

                    ProfileScreen(
                        navController = navController,
                        viewModel = viewModel,
                        username = username
                    )
                }

                composable("login") {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { userId ->
                            navController.navigate("home/$userId") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(
                    route = "add_expense?initialAmount={initialAmount}&userId={userId}",
                    arguments = listOf(
                        navArgument("initialAmount") {
                            type = NavType.StringType
                            defaultValue = "0.00"
                        },
                        navArgument("userId") {
                            type = NavType.IntType
                        }
                    )
                ) { backStackEntry ->
                    val initialAmount = backStackEntry.arguments?.getString("initialAmount") ?: "0.00"
                    val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
                    AddExpense(
                        navController = navController,
                        initialAmount = initialAmount,
                        userId = userId
                    )
                }

            }
        }
    }
}
