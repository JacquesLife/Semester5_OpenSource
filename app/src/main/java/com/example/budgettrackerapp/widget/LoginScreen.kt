/// Reference: https://www.youtube.com/watch?v=-Kj9T1sa6zk
/// The login screen will handle the login and register functionality for the user it will handle
/// some error handling ensuring feels are not left blank then pass then save the user to the database
/// if successful it will navigate to the home screen

package com.example.budgettrackerapp.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.User

@Composable
fun LoginScreen(
    viewModel: BudgetViewModel = viewModel(),
    onLoginSuccess: (String) -> Unit
) {
    // State variables for username and password
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Login form
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Text fields for username and password
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        // Spacer between fields
        Spacer(modifier = Modifier.height(16.dp))

        // Password field with password transformation
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login and register buttons
        Button(
            // Handle login button click
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Username and password cannot be blank"
                } else {
                    errorMessage = ""
                    viewModel.loginUser(username, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            // Handle register button click
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Username and password cannot be blank"
                    // Handle empty fields
                } else {
                    viewModel.registerUser(
                        User(username = username, password = password)
                    ) { success, message ->
                        errorMessage = message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display error message if any
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error
        )
    }
    val loginResult by viewModel.loginResult.collectAsState()
    // Handle successful login
    loginResult?.let { user ->
        onLoginSuccess(user.userId)
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------