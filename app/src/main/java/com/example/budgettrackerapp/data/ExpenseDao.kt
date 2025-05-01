
package com.example.budgettrackerapp.data

import com.example.budgettrackerapp.data.Expense
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userOwnerId = :userId")
            suspend fun getAllExpenses(userId: Int): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category AND userOwnerId = :userId")
    suspend fun getTotalForCategory(category: String, userId: Int): Double
}
