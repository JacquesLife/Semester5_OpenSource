/// Reference: https://www.youtube.com/watch?v=mq8lekRbF4I&list=PL0pXjGnY7POS_IS8gGkwZfxKRMiJ2DSEO&index=2
/// In this page the navigation is setup allowing the user to move between pages with composable functions
/// it also links userid with the navigation

// Fixed AppNavigation.kt
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
import com.example.budgettrackerapp.ui.theme.navbar.MenuDrawer
import com.example.budgettrackerapp.ui.theme.profile.ProfileScreen
import com.example.budgettrackerapp.ui.theme.rewards.RewardsScreen
import com.example.budgettrackerapp.ui.theme.splash.SplashScreen
import com.example.budgettrackerapp.ui.theme.stats.StatsScreen
import com.example.budgettrackerapp.widget.HomeScreen
import com.example.budgettrackerapp.widget.LoginScreen
import com.example.budgettrackerapp.widget.TransactionScreen
import com.example.budgettrackerapp.widget.UpcomingBillsScreen
import kotlinx.coroutines.launch
import com.example.budgettrackerapp.ui.theme.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: BudgetViewModel) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get the current user information
    val loggedInUser = viewModel.loginResult.collectAsState().value
    val currentUserId = loggedInUser?.userId

    // Define routes that don't show bottom navigation
    val routesWithoutBottomNav = setOf(
        "splash",
        "login"
    )

    // Check if current route should show bottom navigation
    val shouldShowBottomNav = currentRoute?.let { route ->
        !routesWithoutBottomNav.contains(route) &&
                !route.startsWith("add_expense") &&
                currentUserId != null // Only show bottom nav when user is logged in
    } ?: false

    // Only show drawer when user is logged in and not on splash/login screens
    if (shouldShowBottomNav && currentUserId != null) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                MenuDrawer(
                    navController = navController,
                    viewModel = viewModel,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { },
                        actions = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                        } else {
                                            drawerState.close()
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavBar(
                        navController = navController,
                        userId = currentUserId.toString()
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
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
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                            TransactionScreen(
                                navController = navController,
                                viewModel = viewModel,
                                userId = userId
                            )
                        }

                        // Upcoming bills screen (acts as home/dashboard)
                        composable(
                            route = "upcoming_bills/{userId}",
                            arguments = listOf(
                                navArgument("userId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                            StatsScreen(
                                navController = navController,
                                viewModel = viewModel,
                                userId = userId
                            )
                        }

                        // Wallet/Rewards screen
                        composable("wallet") {
                            RewardsScreen()
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
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                            SettingsScreen(
                                navController = navController,
                                viewModel = viewModel,
                                userId = userId
                            )
                        }

                        // Help screen (no user ID required)
                        composable("help") {
                            // Create your HelpScreen composable
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
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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
    } else {
        // Show content without drawer for splash and login screens
        Scaffold { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
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
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                        TransactionScreen(
                            navController = navController,
                            viewModel = viewModel,
                            userId = userId
                        )
                    }

                    // Upcoming bills screen (acts as home/dashboard)
                    composable(
                        route = "upcoming_bills/{userId}",
                        arguments = listOf(
                            navArgument("userId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                        StatsScreen(
                            navController = navController,
                            viewModel = viewModel,
                            userId = userId
                        )
                    }

                    // Wallet/Rewards screen (no user ID required)
                    composable("wallet") {
                        RewardsScreen()
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
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                        SettingsScreen(
                            navController = navController,
                            viewModel = viewModel,
                            userId = userId
                        )
                    }

                    // Help screen (no user ID required)
                    composable("help") {
                        // Create your HelpScreen composable
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
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
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