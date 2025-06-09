/// Reference: https://www.youtube.com/watch?v=mq8lekRbF4I&list=PL0pXjGnY7POS_IS8gGkwZfxKRMiJ2DSEO&index=2
/// In this page the navigation is setup allowing the user to move between pages with composable functions
/// it also links userid with the navigation

package com.example.budgettrackerapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.ui.theme.AddExpense
import com.example.budgettrackerapp.ui.theme.UserIdErrorScreen
import com.example.budgettrackerapp.ui.theme.navbar.BottomNavBar
import com.example.budgettrackerapp.ui.theme.navbar.MenuDrawer
import com.example.budgettrackerapp.ui.theme.ProfileScreen
import com.example.budgettrackerapp.ui.theme.rewards.RewardsScreen
import com.example.budgettrackerapp.ui.theme.splash.SplashScreen
import com.example.budgettrackerapp.ui.theme.stats.StatsScreen
import com.example.budgettrackerapp.widget.HomeScreen
import com.example.budgettrackerapp.widget.LoginScreen
import com.example.budgettrackerapp.widget.TransactionScreen
import com.example.budgettrackerapp.widget.UpcomingBillsScreen
import kotlinx.coroutines.launch
import com.example.budgettrackerapp.ui.theme.settings.SettingsScreen
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: BudgetViewModel) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get the current user information
    val loggedInUser = viewModel.loginResult.collectAsState().value
    loggedInUser?.userId

    // Define routes that don't show bottom navigation
    setOf(
        "splash",
        "login"
    )

    // Main Scaffold with conditional bottom navigation
    Scaffold(
        topBar = {
            if (currentRoute != null &&
                currentRoute != "splash" &&
                currentRoute != "login" &&
                !currentRoute.startsWith("add_expense")
            ) {
                TopAppBar(
                    title = { },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = {
            if (currentRoute != null &&
                currentRoute != "splash" &&
                currentRoute != "login" &&
                !currentRoute.startsWith("add_expense")
            ) {
                BottomNavBar(navController = navController, userId = loggedInUser?.userId ?: "")
            }
        }
    ) { innerPadding ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                MenuDrawer(
                    navController = navController,
                    viewModel = viewModel,
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            Box(modifier = Modifier.padding(innerPadding)) {
                // Single Navigation graph
                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    // Splash screen
                    composable("splash") {
                        SplashScreen(navController)
                    }

                    // Login screen
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

                    // Home screen with user ID
                    composable(
                        route = "home/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
                        HomeScreen(
                            navController = navController,
                            viewModel = viewModel,
                            userId = userId
                        )
                    }

                    // Transaction screen with user ID
                    composable(
                        route = "transaction/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
                        TransactionScreen(
                            navController = navController,
                            viewModel = viewModel,
                            userId = userId
                        )
                    }

                    // Upcoming bills screen with user ID
                    composable(
                        route = "upcoming_bills/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
                        UpcomingBillsScreen(
                            navController = navController,
                            viewModel = viewModel,
                            userId = userId
                        )
                    }

                    // Statistics screen with user ID
                    composable(
                        route = "stats/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
                        StatsScreen(
                            viewModel = viewModel,
                            userId = userId
                        )
                    }

                    // Wallet/Rewards screen with user ID
                    composable(
                        route = "wallet/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
                        RewardsScreen(
                            viewModel = viewModel
                        )
                    }

                    // Profile screen with user ID
                    composable(
                        route = "profile/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
                        val loggedInUser by viewModel.loginResult.collectAsState()
                        val username = loggedInUser?.username ?: "User"

                        ProfileScreen(
                            navController = navController,
                            viewModel = viewModel,
                            username = username
                        )
                    }

                    // Settings screen with user ID
                    composable(
                        route = "settings/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
                        SettingsScreen()
                    }

                    // Help screen (no user ID required)
                    composable("help") {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Help & Support Screen")
                        }
                    }

                    // Add expense screen with optional initial amount and required user ID
                    composable(
                        route = "add_expense?initialAmount={initialAmount}&userId={userId}",
                        arguments = listOf(
                            navArgument("initialAmount") {
                                type = NavType.StringType
                                defaultValue = "0.00"
                            },
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val initialAmount = backStackEntry.arguments?.getString("initialAmount") ?: "0.00"
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId.isNullOrBlank()) {
                            UserIdErrorScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                            return@composable
                        }
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
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------