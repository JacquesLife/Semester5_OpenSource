
package com.example.budgettrackerapp.data

import com.example.budgettrackerapp.data.Expense
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


// Data Access Object (DAO) for handling operations related to the 'expenses' table
@Dao
interface ExpenseDao {

    // Inserts a new expense into the database
    @Insert
    suspend fun insert(expense: Expense)

    // Retrieves all expenses that belong to a specific user
    @Query("SELECT * FROM expenses WHERE userOwnerId = :userId")
    suspend fun getAllExpenses(userId: Int): List<Expense>

    // Calculates the total amount spent in a specific category by a particular user
    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category AND userOwnerId = :userId")
    suspend fun getTotalForCategory(category: String, userId: Int): Double
}
