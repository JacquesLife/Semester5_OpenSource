package com.example.budgettrackerapp.data

// Repository class that acts as a mediator between ViewModel and Firebase services
class BudgetRepository(
    private val userService: FirebaseUserService = FirebaseUserService(),
    private val expenseService: FirebaseExpenseService = FirebaseExpenseService(),
    private val budgetSettingsService: FirebaseBudgetSettingsService = FirebaseBudgetSettingsService()
) {

    // Registers a new user; returns true if successful, false otherwise
    suspend fun registerUser(user: User): Boolean {
        return try {
            // Check if username already exists
            if (userService.isUsernameExists(user.username)) {
                false // Username already exists
            } else {
                userService.insertUser(user)  // Insert user into Firebase
            }
        } catch (e: Exception) {
            e.printStackTrace()       // Print error if insertion fails
            false
        }
    }

    // Logs in user by fetching the user record by username
    suspend fun loginUser(username: String): User? {
        return try {
            userService.login(username)   // Attempt to retrieve user from Firebase
        } catch (e: Exception) {
            e.printStackTrace()
            null                      // Return null if user not found or error occurs
        }
    }

    // Adds a new expense record for the user
    suspend fun addExpense(expense: Expense): String? {
        return try {
            expenseService.insert(expense)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Loads all expenses for a specific user
    suspend fun loadExpenses(userId: String): List<Expense> {
        return try {
            expenseService.getAllExpenses(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Calculates the total amount spent in a given category by a specific user
    suspend fun getTotalForCategory(category: String, userId: String): Double {
        return try {
            expenseService.getTotalForCategory(category, userId)
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    // Saves the user's monthly budget settings
    suspend fun saveBudgetSettings(settings: BudgetSettings): Boolean {
        return try {
            budgetSettingsService.insert(settings)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Retrieves budget settings for a specific user
    suspend fun getBudgetSettings(userId: String): BudgetSettings? {
        return try {
            budgetSettingsService.getSettings(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Retrieves the most recent budget settings (for backwards compatibility)
    suspend fun getBudgetSettings(): BudgetSettings? {
        return try {
            budgetSettingsService.getSettings()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Delete an expense
    suspend fun deleteExpense(expenseId: String): Boolean {
        return try {
            expenseService.deleteExpense(expenseId)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Update an expense
    suspend fun updateExpense(expense: Expense): Boolean {
        return try {
            expenseService.updateExpense(expense)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Update budget settings
    suspend fun updateBudgetSettings(settings: BudgetSettings): Boolean {
        return try {
            budgetSettingsService.updateSettings(settings)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
