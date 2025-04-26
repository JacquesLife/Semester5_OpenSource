package com.example.budgettrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.ui.theme.BudgetTrackerAppTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgettrackerapp.widget.HomeScreen
import com.example.budgettrackerapp.widget.LoginScreen
import com.example.budgettrackerapp.widget.SplashScreen
import com.example.budgettrackerapp.widget.TransactionScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetTrackerAppTheme {
                Surface {
                    val viewModel: BudgetViewModel = viewModel()
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

