package com.example.budgettrackerapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BudgetRepository(
        db.userDao(),
        db.expenseDao(),
        db.budgetSettingsDao()
    )

    // Change from LiveData to StateFlow
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> get() = _expenses

    private val _loginResult = MutableStateFlow<User?>(null)
    val loginResult: StateFlow<User?> get() = _loginResult

    private val _budgetSettings = MutableStateFlow<BudgetSettings?>(null)
    val budgetSettings: StateFlow<BudgetSettings?> get() = _budgetSettings

    fun registerUser(user: User) = viewModelScope.launch {
        repository.registerUser(user)
    }

    fun loginUser(username: String, password: String) = viewModelScope.launch {
        _loginResult.value = repository.loginUser(username, password)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.addExpense(expense)
    }

    fun loadExpenses() = viewModelScope.launch {
        _expenses.value = repository.loadExpenses()
    }

    fun getTotalForCategory(category: String, callback: (Double) -> Unit) {
        viewModelScope.launch {
            val total = repository.getTotalForCategory(category)
            callback(total)
        }
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
