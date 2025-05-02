package com.example.budgettrackerapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

// ViewModel that handles the UI-related logic and communicates with the repository
class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    // Access the singleton instance of the Room database
    private val db = AppDatabase.getDatabase(application)

    // Initialize the repository using DAOs from the database
    private val repository = BudgetRepository(
        db.userDao(),
        db.expenseDao(),
        db.budgetSettingsDao()
    )

    // StateFlow to hold and expose the list of expenses
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> get() = _expenses

    // StateFlow to hold and expose the logged-in user (null if not logged in)
    private val _loginResult = MutableStateFlow<User?>(null)
    val loginResult: StateFlow<User?> get() = _loginResult

    // StateFlow to hold and expose the current budget settings
    private val _budgetSettings = MutableStateFlow<BudgetSettings?>(null)
    val budgetSettings: StateFlow<BudgetSettings?> get() = _budgetSettings

    // Handles user registration with duplicate username check and password hashing
    fun registerUser(user: User, onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        val existing = repository.loginUser(user.username)
        if (existing != null) {
            // Username already taken
            onResult(false, "Username already exists. Please log in.")
        } else {
            // Hash the password securely using BCrypt
            val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
            val secureUser = user.copy(password = hashedPassword)

            // Register the user in the repository
            val success = repository.registerUser(secureUser)
            onResult(success, if (success) "Registration successful." else "Registration failed.")
        }
    }

    // Handles user login by checking username and verifying the password hash
    fun loginUser(username: String, password: String) = viewModelScope.launch {
        val user = repository.loginUser(username)
        if (user != null && BCrypt.checkpw(password, user.password)) {
            _loginResult.value = user // Login successful
        } else {
            _loginResult.value = null // Login failed
        }
    }

    // Adds an expense to the database
    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.addExpense(expense)
    }

    // Loads all expenses for a specific user and updates the StateFlow
    fun loadExpenses(userId: Int) = viewModelScope.launch {
        _expenses.value = repository.loadExpenses(userId)
    }

    // Saves the user's monthly budget settings
    fun saveBudgetSettings(settings: BudgetSettings) = viewModelScope.launch {
        repository.saveBudgetSettings(settings)
    }

    // Loads the user's budget settings and updates the StateFlow
    fun loadBudgetSettings() = viewModelScope.launch {
        _budgetSettings.value = repository.getBudgetSettings()
    }

    // Clears the logged-in user (used during logout)
    fun logout() {
        _loginResult.value = null
    }
}
