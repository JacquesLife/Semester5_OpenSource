
package com.example.budgettrackerapp.data


class BudgetRepository(
    private val userDao: UserDao,
    private val expenseDao: ExpenseDao,
    private val budgetSettingsDao: BudgetSettingsDao
) {
    suspend fun registerUser(user: User): Boolean {
        return try {
            userDao.insertUser(user)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun loginUser(username: String): User? {
        return try {
            userDao.login(username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense)
    }
    suspend fun loadExpenses(userId: Int): List<Expense> {
        return expenseDao.getAllExpenses(userId)
    }


    suspend fun getTotalForCategory(category: String, userId: Int): Double {
        return expenseDao.getTotalForCategory(category, userId)
    }

    suspend fun saveBudgetSettings(settings: BudgetSettings) {
        budgetSettingsDao.insert(settings)
    }

    suspend fun getBudgetSettings(): BudgetSettings? {
        return budgetSettingsDao.getSettings()
    }
}
