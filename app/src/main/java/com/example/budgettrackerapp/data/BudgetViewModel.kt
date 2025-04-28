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
        db.categoryDao(),
        db.expenseDao()
    )

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> get() = _loginResult

    // ADD THESE:
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

    fun loadCategories(username: String) = viewModelScope.launch {
        _categories.value = repository.getCategoriesForUser(username)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.addCategory(category)
        loadCategories(category.username)
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.addExpense(expense)
    }

    fun loadExpenses(start: String, end: String) = viewModelScope.launch {
        _expenses.value = repository.getExpensesBetweenDates(start, end)
    }

    fun getTotalForCategory(catId: Int, start: String, end: String, callback: (Double) -> Unit) {
        viewModelScope.launch {
            val total = repository.getTotalForCategory(catId, start, end)
            callback(total)
        }
    }
}