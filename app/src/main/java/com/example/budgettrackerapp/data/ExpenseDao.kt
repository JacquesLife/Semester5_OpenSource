
package com.example.budgettrackerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end")
    suspend fun getBetweenDates(start: String, end: String): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category AND date BETWEEN :start AND :end")
    suspend fun getTotalForCategory(category: String, start: String, end: String): Double
}
