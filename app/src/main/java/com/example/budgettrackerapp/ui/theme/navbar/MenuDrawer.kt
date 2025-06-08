// Fixed MenuDrawer.kt
package com.example.budgettrackerapp.ui.theme.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector? = null,
    val iconResId: Int? = null,
    val route: String,
    val requiresUserId: Boolean = true
)

@Composable
fun MenuDrawer(
    navController: NavController,
    viewModel: BudgetViewModel,
    onCloseDrawer: () -> Unit
) {
    val loggedInUser by viewModel.loginResult.collectAsState()
    val currentUsername = loggedInUser?.username ?: "User"
    val userId = loggedInUser?.userId ?: 0

    // Drawer menu items
    val drawerItems = listOf(
        DrawerMenuItem(
            title = "Home",
            icon = Icons.Filled.Home,
            route = "upcoming_bills",
            requiresUserId = true
        ),
        DrawerMenuItem(
            title = "Transactions",
            iconResId = R.drawable.chart,
            route = "transaction",
            requiresUserId = true
        ),
        DrawerMenuItem(
            title = "Statistics",
            iconResId = R.drawable.chart,
            route = "stats",
            requiresUserId = true
        ),
        DrawerMenuItem(
            title = "Wallet",
            iconResId = R.drawable.wallet,
            route = "wallet",
            requiresUserId = true
        ),
        DrawerMenuItem(
            title = "Profile",
            iconResId = R.drawable.user,
            route = "profile",
            requiresUserId = true
        ),
        DrawerMenuItem(
            title = "Settings",
            icon = Icons.Filled.Settings,
            route = "settings",
            requiresUserId = true
        ),
        DrawerMenuItem(
            title = "Help & Support",
            icon = Icons.Filled.Help,
            route = "help",
            requiresUserId = false
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Drawer header with user info
        DrawerHeader(username = currentUsername)

        Spacer(modifier = Modifier.height(16.dp))

        // Drawer menu items
        LazyColumn {
            items(drawerItems) { item ->
                DrawerItem(
                    item = item,
                    userId = userId,
                    navController = navController,
                    onCloseDrawer = onCloseDrawer
                )
            }

            // Logout item at the bottom
            item {
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                LogoutItem(
                    userId = userId,
                    navController = navController,
                    viewModel = viewModel,
                    onCloseDrawer = onCloseDrawer
                )
            }
        }
    }
}

@Composable
fun DrawerHeader(username: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture placeholder
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.take(1).uppercase(),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "Welcome back!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = username,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DrawerItem(
    item: DrawerMenuItem,
    userId: Comparable<*>,
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    val route = if (item.requiresUserId) "${item.route}/$userId" else item.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                }
                onCloseDrawer()
            }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        when {
            item.icon != null -> {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            item.iconResId != null -> {
                Icon(
                    painter = painterResource(id = item.iconResId),
                    contentDescription = item.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Title
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LogoutItem(
    userId: Comparable<*>,
    navController: NavController,
    viewModel: BudgetViewModel,
    onCloseDrawer: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
                onCloseDrawer()
            }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.ExitToApp,
            contentDescription = "Logout",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = "Logout",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------