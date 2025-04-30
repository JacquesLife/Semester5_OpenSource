
package com.example.budgettrackerapp.data

class BudgetRepository(
    private val userDao: UserDao,
    private val expenseDao: ExpenseDao,
    private val budgetSettingsDao: BudgetSettingsDao
) {
    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(username: String, password: String): User? {
        return userDao.login(username, password)
    }

    suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense)
    }
    suspend fun loadExpenses(): List<Expense> {
        return expenseDao.getAllExpenses()
    }


    suspend fun getTotalForCategory(category: String): Double {
        return expenseDao.getTotalForCategory(category)
    }

    suspend fun saveBudgetSettings(settings: BudgetSettings) {
        budgetSettingsDao.insert(settings)
    }

    suspend fun getBudgetSettings(): BudgetSettings? {
        return budgetSettingsDao.getSettings()
    }
}
