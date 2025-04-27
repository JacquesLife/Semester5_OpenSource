package com.example.budgettrackerapp.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.budgettrackerapp.data.BudgetViewModel

@Composable
fun HomeScreen(viewModel: BudgetViewModel, navController: NavHostController) {
    // Automatically navigate to the transaction screen when HomeScreen is loaded
    LaunchedEffect(Unit) {
        // This will navigate to the "transaction" screen as soon as the HomeScreen is shown
        navController.navigate("transaction")
    }
}