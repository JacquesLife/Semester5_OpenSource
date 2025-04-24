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

    @Query("SELECT SUM(amount) FROM expenses WHERE categoryId = :catId AND date BETWEEN :start AND :end")
    suspend fun getTotalForCategory(catId: Int, start: String, end: String): Double
}