
package com.example.budgettrackerapp.data


// Repository class that acts as a mediator between ViewModel and Room DAOs
class BudgetRepository(
    private val userDao: UserDao,                      // Handles user data operations
    private val expenseDao: ExpenseDao,                // Handles expense data operations
    private val budgetSettingsDao: BudgetSettingsDao   // Handles budget settings operations
) {

    // Registers a new user; returns true if successful, false otherwise
    suspend fun registerUser(user: User): Boolean {
        return try {
            userDao.insertUser(user)  // Insert user into database
            true
        } catch (e: Exception) {
            e.printStackTrace()       // Print error if insertion fails
            false
        }
    }

    // Logs in user by fetching the user record by username
    suspend fun loginUser(username: String): User? {
        return try {
            userDao.login(username)   // Attempt to retrieve user
        } catch (e: Exception) {
            e.printStackTrace()
            null                      // Return null if user not found or error occurs
        }
    }

    // Adds a new expense record for the user
    suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense)
    }

    // Loads all expenses for a specific user
    suspend fun loadExpenses(userId: Int): List<Expense> {
        return expenseDao.getAllExpenses(userId)
    }

    // Calculates the total amount spent in a given category by a specific user
    suspend fun getTotalForCategory(category: String, userId: Int): Double {
        return expenseDao.getTotalForCategory(category, userId)
    }

    // Saves the user's monthly budget settings
    suspend fun saveBudgetSettings(settings: BudgetSettings) {
        budgetSettingsDao.insert(settings)
    }

    // Retrieves the most recent budget settings (if any)
    suspend fun getBudgetSettings(): BudgetSettings? {
        return budgetSettingsDao.getSettings()
    }
}
