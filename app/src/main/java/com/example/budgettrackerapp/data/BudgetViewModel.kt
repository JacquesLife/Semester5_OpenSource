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

    fun registerUser(user: User, onResult: (Boolean, String) -> Unit) = viewModelScope.launch {
        val existing = repository.loginUser(user.username)
        if (existing != null) {
            onResult(false, "Username already exists. Please log in.")
        } else {
            val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
            val secureUser = user.copy(password = hashedPassword)
            val success = repository.registerUser(secureUser)
            onResult(success, if (success) "Registration successful." else "Registration failed.")
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
    }

    fun saveBudgetSettings(settings: BudgetSettings) = viewModelScope.launch {
        repository.saveBudgetSettings(settings)
    }

    fun loadBudgetSettings() = viewModelScope.launch {
        _budgetSettings.value = repository.getBudgetSettings()
    }

    fun logout() {
        _loginResult.value = null
    }
}
