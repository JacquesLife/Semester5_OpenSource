/// Reference: https://www.youtube.com/watch?v=-Kj9T1sa6zk
/// The login screen will handle the login and register functionality for the user it will handle
/// some error handling ensuring feels are not left blank then pass then save the user to the database
/// if successful it will navigate to the home screen

package com.example.budgettrackerapp.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.User

// Data class to hold validation state
data class ValidationState(
    val isValid: Boolean = false,
    val errorMessage: String = ""
)

@Composable
fun LoginScreen(
    viewModel: BudgetViewModel = viewModel(),
    onLoginSuccess: (String) -> Unit
) {
    // State variables for input fields
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(true) }

    // Validation states
    var usernameValidation by remember { mutableStateOf(ValidationState()) }
    var passwordValidation by remember { mutableStateOf(ValidationState()) }
    var generalErrorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Real-time validation functions
    fun validateUsername(input: String): ValidationState {
        return when {
            input.isBlank() -> ValidationState(false, "Username is required")
            input.length < 3 -> ValidationState(false, "Username must be at least 3 characters")
            input.length > 20 -> ValidationState(false, "Username must be less than 20 characters")
            !input.matches(Regex("^[a-zA-Z0-9_]+$")) -> ValidationState(false, "Username can only contain letters, numbers, and underscores")
            input.startsWith("_") || input.endsWith("_") -> ValidationState(false, "Username cannot start or end with underscore")
            else -> ValidationState(true, "")
        }
    }

    fun validatePassword(input: String, isRegistering: Boolean = !isLoginMode): ValidationState {
        return when {
            input.isBlank() -> ValidationState(false, "Password is required")
            isRegistering && input.length < 6 -> ValidationState(false, "Password must be at least 6 characters")
            isRegistering && !input.any { it.isDigit() } -> ValidationState(false, "Password must contain at least one number")
            isRegistering && !input.any { it.isLetter() } -> ValidationState(false, "Password must contain at least one letter")
            else -> ValidationState(true, "")
        }
    }

    // Update validations when inputs change
    LaunchedEffect(username) {
        usernameValidation = validateUsername(username)
    }

    LaunchedEffect(password, isLoginMode) {
        passwordValidation = validatePassword(password, !isLoginMode)
    }

    // Clear general error when switching modes
    LaunchedEffect(isLoginMode) {
        generalErrorMessage = ""
        isLoading = false
    }

    // Login form
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = if (isLoginMode) "Welcome Back" else "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Username field with validation
        OutlinedTextField(
            value = username,
            onValueChange = { username = it.trim() },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            isError = !usernameValidation.isValid && username.isNotEmpty(),
            supportingText = {
                if (!usernameValidation.isValid && username.isNotEmpty()) {
                    Text(
                        text = usernameValidation.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field with validation and visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = !passwordValidation.isValid && password.isNotEmpty(),
            supportingText = {
                if (!passwordValidation.isValid && password.isNotEmpty()) {
                    Text(
                        text = passwordValidation.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                } else if (!isLoginMode && password.isNotEmpty() && passwordValidation.isValid) {
                    Text(
                        text = "✓ Password meets requirements",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            singleLine = true
        )

        if (!isLoginMode) {
            // Password requirements for registration
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Password Requirements:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val requirements = listOf(
                        "At least 6 characters" to (password.length >= 6),
                        "Contains at least one letter" to password.any { it.isLetter() },
                        "Contains at least one number" to password.any { it.isDigit() }
                    )

                    requirements.forEach { (requirement, met) ->
                        Text(
                            text = "${if (met) "✓" else "○"} $requirement",
                            fontSize = 11.sp,
                            color = if (met) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main action button (Login/Register)
        Button(
            onClick = {
                val isFormValid = usernameValidation.isValid && passwordValidation.isValid

                if (!isFormValid) {
                    generalErrorMessage = "Please fix the errors above"
                    return@Button
                }

                isLoading = true
                generalErrorMessage = ""

                if (isLoginMode) {
                    // Login
                    viewModel.loginUser(username, password)
                } else {
                    // Register
                    viewModel.registerUser(
                        User(username = username, password = password)
                    ) { success, message ->
                        isLoading = false
                        if (success) {
                            generalErrorMessage = "Registration successful! Please log in."
                            isLoginMode = true
                            password = ""
                        } else {
                            generalErrorMessage = message
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && usernameValidation.isValid && passwordValidation.isValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoginMode) "Login" else "Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch between login and register
        OutlinedButton(
            onClick = {
                isLoginMode = !isLoginMode
                generalErrorMessage = ""
                // Clear password when switching from register to login for better UX
                if (isLoginMode) password = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Need an account? Register" else "Already have an account? Login")
        }

        // Display general error messages
        if (generalErrorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (generalErrorMessage.contains("successful"))
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = generalErrorMessage,
                    modifier = Modifier.padding(12.dp),
                    color = if (generalErrorMessage.contains("successful"))
                        Color(0xFF4CAF50)
                    else
                        MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Handle login result
    val loginResult by viewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult) {
        if (loginResult != null) {
            isLoading = false
            onLoginSuccess(loginResult!!.userId)
        } else if (isLoginMode && !isLoading) {
            // Only show error if we were trying to login and it failed
            if (username.isNotEmpty() && password.isNotEmpty() && usernameValidation.isValid && passwordValidation.isValid) {
                generalErrorMessage = "Invalid username or password"
            }
        }
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------