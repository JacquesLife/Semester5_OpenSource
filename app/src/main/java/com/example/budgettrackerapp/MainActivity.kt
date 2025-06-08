// References: https://medium.com/@acceldia/jetpack-compose-creating-expandable-cards-with-content-9ea1eae09efe
/// This file is responsible for requesting the storage permissions on startup
/// It will allow the user to add a receipt image to their expense

package com.example.budgettrackerapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.ExpenseNotificationManager
import com.example.budgettrackerapp.ui.theme.BudgetTrackerAppTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
        // Handle permission result
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (!allGranted) {
            val deniedPermissions = permissions.filterValues { !it }.keys
            if (deniedPermissions.any { it.contains("MEDIA") || it.contains("STORAGE") }) {
                Toast.makeText(this, "Storage permissions are required to upload photos", Toast.LENGTH_LONG).show()
            }
            if (deniedPermissions.contains(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(this, "Notification permission is required for expense reminders", Toast.LENGTH_LONG).show()
            }
        } else {
            // Initialize notification system when permissions are granted
            initializeNotificationSystem()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions on startup
        requestPermissions()

        setContent {
            BudgetTrackerAppTheme {
                Surface {
                    val viewModel: BudgetViewModel = viewModel()
                    AppNavigation(viewModel)
                }
            }
        }
        
        // Try to initialize notification system even if some permissions were denied
        // The notification system will handle missing permissions gracefully
        initializeNotificationSystem()
    }

    private fun initializeNotificationSystem() {
        val notificationManager = ExpenseNotificationManager(this)
        notificationManager.scheduleNotificationChecks()
    }

    // Request storage and notification permissions
    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        // Storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        // Notification permissions (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }
}

@Composable
// Request storage permissions for may be needed in the future
fun RequestStoragePermissions() {
    val context = LocalContext.current

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }
        if (!allGranted) {
            Toast.makeText(context, "Storage permissions are required to upload photos", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(key1 = true) {
        launcher.launch(permissionsToRequest)
    }
}