/// Reference: https://www.youtube.com/watch?v=Q0gRqbtFLcw
/// This is a basic splash screen that will display the app logo and a welcome message
/// We have a buck as our logo it will display for 3 seconds and then navigate to the login screen

package com.example.budgettrackerapp.ui.theme.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.ui.theme.InterFont
import kotlinx.coroutines.delay



@Composable
fun SplashScreen(navController: NavController) {
    // Simulate a delay for the splash screen
    LaunchedEffect(key1 = true) {
        delay(3000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Splash screen content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D47A1)),
        contentAlignment = Alignment.Center
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize()
        )

        Column(
            // Center the logo and text vertically
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Buck svg display
            Image(
                painter = painterResource(id = R.drawable.buck),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                // Welcoming text
                text = "Welcome to Buck Savers",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------