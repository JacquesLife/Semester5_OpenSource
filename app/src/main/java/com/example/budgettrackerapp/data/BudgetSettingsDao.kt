package com.example.budgettrackerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BudgetSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: BudgetSettings)

    @Query("SELECT * FROM budget_settings WHERE userId = :userId LIMIT 1")
    suspend fun getSettings(userId: Int): BudgetSettings?
}
