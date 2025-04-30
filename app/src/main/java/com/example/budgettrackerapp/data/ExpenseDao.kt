
package com.example.budgettrackerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses")
            suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category")
    suspend fun getTotalForCategory(category: String): Double
}
