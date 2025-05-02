package com.example.budgettrackerapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BudgetRepository(
        db.userDao(),
        db.expenseDao(),
        db.budgetSettingsDao()
    )

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> get() = _expenses

    private val _loginResult = MutableStateFlow<User?>(null)
    val loginResult: StateFlow<User?> get() = _loginResult

    private val _budgetSettings = MutableStateFlow<BudgetSettings?>(null)
    val budgetSettings: StateFlow<BudgetSettings?> get() = _budgetSettings

    private val _savingsPercentage = MutableStateFlow(0.0)
    val savingsPercentage: StateFlow<Double> get() = _savingsPercentage

    private val _rewardPoints = MutableStateFlow(0)
    val rewardPoints: StateFlow<Int> get() = _rewardPoints

    fun registerUser(user: User, onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        try {
            val existing = repository.loginUser(user.username)
            if (existing != null) {
                onResult(false, "Username already exists. Please log in.")
            } else {
                val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
                val secureUser = user.copy(password = hashedPassword)
                val success = repository.registerUser(secureUser)
                onResult(success, if (success) "Registration successful." else "Registration failed. Please try again.")
            }
        } catch (e: Exception) {
            // Log the exception
            e.printStackTrace()
            onResult(false, "Registration error: ${e.message ?: "Unknown error"}")
        }
    }

    fun loginUser(username: String, password: String) = viewModelScope.launch {
        val user = repository.loginUser(username)
        if (user != null && BCrypt.checkpw(password, user.password)) {
            _loginResult.value = user
        } else {
            _loginResult.value = null
        }
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.addExpense(expense)
    }

    fun loadExpenses(userId: Int) = viewModelScope.launch {
        _expenses.value = repository.loadExpenses(userId)
        calculateSavingsAndRewards(userId)
    }

    fun saveBudgetSettings(settings: BudgetSettings) = viewModelScope.launch {
        repository.saveBudgetSettings(settings)
    }

    fun loadBudgetSettings(userId: Int) = viewModelScope.launch {
        _budgetSettings.value = repository.getBudgetSettings(userId)
    }

    fun logout() {
        _loginResult.value = null
    }

    fun calculateSavingsAndRewards(userId: Int) = viewModelScope.launch {
        println("DEBUG: Calculating savings for user: $userId")

        val expenses = repository.loadExpenses(userId)
        println("DEBUG: Found ${expenses.size} expenses")

        val totalSpent = expenses.sumOf { it.amount }
        println("DEBUG: Total spent: $totalSpent")

        val settings = repository.getBudgetSettings(userId)
        println("DEBUG: Budget settings: $settings")

        if (settings != null) {
            val savings = settings.monthlyBudget - totalSpent
            println("DEBUG: Savings amount: $savings")

            val percentage = if (settings.monthlyBudget > 0) {
                (savings / settings.monthlyBudget) * 100
            } else 0.0
            println("DEBUG: Savings percentage: $percentage%")

            _savingsPercentage.value = percentage

            val newPoints = when {
                percentage >= settings.monthlyMaxGoal -> {
                    println("DEBUG: Awarding 100 points (exceeded max goal)")
                    100
                }
                percentage >= settings.monthlyMinGoal -> {
                    println("DEBUG: Awarding 50 points (exceeded min goal)")
                    50
                }
                else -> {
                    println("DEBUG: Awarding 0 points (below min goal)")
                    0
                }
            }

            println("DEBUG: Setting reward points to: $newPoints")
            _rewardPoints.value = newPoints
        } else {
            println("DEBUG: No budget settings found for user $userId")
        }
    }
}
