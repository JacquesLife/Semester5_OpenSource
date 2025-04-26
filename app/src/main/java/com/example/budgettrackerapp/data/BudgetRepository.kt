package com.example.budgettrackerapp.data

class BudgetRepository(
    private val userDao: UserDao,
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao
) {

    // ----------------- USERS -----------------

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(username: String, password: String): User? {
        return userDao.login(username, password)
    }

    // ----------------- CATEGORIES -----------------

    suspend fun addCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun getCategoriesForUser(username: String): List<Category> {
        return categoryDao.getAll(username)
    }

    // ----------------- EXPENSES -----------------

    suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun getExpensesBetweenDates(start: String, end: String): List<Expense> {
        return expenseDao.getBetweenDates(start, end)
    }

    suspend fun getTotalForCategory(catId: Int, start: String, end: String): Double {
        return expenseDao.getTotalForCategory(catId, start, end)
    }
}
