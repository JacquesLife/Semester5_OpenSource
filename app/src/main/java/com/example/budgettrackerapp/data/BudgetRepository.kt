
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

    suspend fun getExpensesBetweenDates(start: String, end: String): List<Expense> {
        return expenseDao.getBetweenDates(start, end)
    }

    suspend fun getTotalForCategory(category: String, start: String, end: String): Double {
        return expenseDao.getTotalForCategory(category, start, end)
    }

    suspend fun saveBudgetSettings(settings: BudgetSettings) {
        budgetSettingsDao.insert(settings)
    }

    suspend fun getBudgetSettings(): BudgetSettings? {
        return budgetSettingsDao.getSettings()
    }
}
