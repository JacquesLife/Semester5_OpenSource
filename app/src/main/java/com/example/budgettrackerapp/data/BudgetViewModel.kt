
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
        db.expenseDao()
    )

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> get() = _loginResult



    private val _yearlyBudget = MutableLiveData<Double>()
    val yearlyBudget: LiveData<Double> get() = _yearlyBudget

    private val _monthlyMaxGoal = MutableLiveData<Double>()
    val monthlyMaxGoal: LiveData<Double> get() = _monthlyMaxGoal

    private val _monthlyMinGoal = MutableLiveData<Double>()
    val monthlyMinGoal: LiveData<Double> get() = _monthlyMinGoal

    fun setBudget(yearlyBudget: Double, monthlyMaxGoal: Double, monthlyMinGoal: Double) {
        _yearlyBudget.value = yearlyBudget
        _monthlyMaxGoal.value = monthlyMaxGoal
        _monthlyMinGoal.value = monthlyMinGoal
    }

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
}
