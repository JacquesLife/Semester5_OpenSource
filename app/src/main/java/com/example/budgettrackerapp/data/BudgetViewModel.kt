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

    // Initialize the repository using Firebase services
    private val repository = BudgetRepository()

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
        // Hash the password securely using BCrypt
        val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
        val secureUser = user.copy(password = hashedPassword)

        // Register the user in the repository (it will check for duplicates)
        val success = repository.registerUser(secureUser)
        onResult(success, if (success) "Registration successful." else "Username already exists. Please choose a different username.")
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
    fun addExpense(expense: Expense, onResult: (Boolean, String?) -> Unit = { _, _ -> }) = viewModelScope.launch {
        val expenseId = repository.addExpense(expense)
        onResult(expenseId != null, expenseId)
    }

    // Loads all expenses for a specific user and updates the StateFlow
    fun loadExpenses(userId: String) = viewModelScope.launch {
        _expenses.value = repository.loadExpenses(userId)
    }

    // Saves the user's monthly budget settings
    fun saveBudgetSettings(settings: BudgetSettings, onResult: (Boolean) -> Unit = {}) = viewModelScope.launch {
        val success = repository.saveBudgetSettings(settings)
        onResult(success)
    }

    // Loads the user's budget settings for a specific user and updates the StateFlow
    fun loadBudgetSettings(userId: String) = viewModelScope.launch {
        _budgetSettings.value = repository.getBudgetSettings(userId)
    }

    // Loads the user's budget settings (backwards compatibility) and updates the StateFlow
    fun loadBudgetSettings() = viewModelScope.launch {
        _budgetSettings.value = repository.getBudgetSettings()
    }

    // Delete an expense
    fun deleteExpense(expenseId: String, onResult: (Boolean) -> Unit = {}) = viewModelScope.launch {
        val success = repository.deleteExpense(expenseId)
        onResult(success)
    }

    // Update an expense
    fun updateExpense(expense: Expense, onResult: (Boolean) -> Unit = {}) = viewModelScope.launch {
        val success = repository.updateExpense(expense)
        onResult(success)
    }

    // Update budget settings
    fun updateBudgetSettings(settings: BudgetSettings, onResult: (Boolean) -> Unit = {}) = viewModelScope.launch {
        val success = repository.updateBudgetSettings(settings)
        onResult(success)
    }

    // Get total for category
    fun getTotalForCategory(category: String, userId: String, onResult: (Double) -> Unit) = viewModelScope.launch {
        val total = repository.getTotalForCategory(category, userId)
        onResult(total)
    }

    // Clears the logged-in user (used during logout)
    fun logout() {
        _loginResult.value = null
    }
}
