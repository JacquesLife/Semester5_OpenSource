package com.example.budgettrackerapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.widget.SplashScreen
import com.example.budgettrackerapp.widget.HomeScreen
import com.example.budgettrackerapp.widget.LoginScreen
import com.example.budgettrackerapp.widget.TransactionScreen



@Composable
fun AppNavigation(viewModel: BudgetViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable(route = "splash") {
            SplashScreen(navController)
        }
        composable(route = "login") {
            LoginScreen(viewModel = viewModel) {
                // After successful login, navigate to HomeScreen
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        composable(route = "home") {
            HomeScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(route = "transaction_screen") {
            TransactionScreen()
        }
    }
}
