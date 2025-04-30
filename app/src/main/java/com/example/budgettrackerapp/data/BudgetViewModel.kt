
package com.example.budgettrackerapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BudgetRepository(
        db.userDao(),
        db.expenseDao(),
        db.budgetSettingsDao()
    )

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> get() = _loginResult

    private val _budgetSettings = MutableLiveData<BudgetSettings?>()
    val budgetSettings: LiveData<BudgetSettings?> get() = _budgetSettings

    fun registerUser(user: User) = viewModelScope.launch {
        repository.registerUser(user)
    }

    fun loginUser(username: String, password: String) = viewModelScope.launch {
        _loginResult.value = repository.loginUser(username, password)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.addExpense(expense)
    }

    fun loadExpenses(start: String, end: String) = viewModelScope.launch {
        _expenses.value = repository.getExpensesBetweenDates(start, end)
    }

    fun getTotalForCategory(category: String, start: String, end: String, callback: (Double) -> Unit) {
        viewModelScope.launch {
            val total = repository.getTotalForCategory(category, start, end)
            callback(total)
        }
    }

    fun saveBudgetSettings(settings: BudgetSettings) = viewModelScope.launch {
        repository.saveBudgetSettings(settings)
    }

    fun loadBudgetSettings() = viewModelScope.launch {
        _budgetSettings.value = repository.getBudgetSettings()
    }
}
