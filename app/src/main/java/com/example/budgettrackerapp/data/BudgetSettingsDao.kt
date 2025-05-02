
package com.example.budgettrackerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Data Access Object for interacting with the BudgetSettings table
@Dao
interface BudgetSettingsDao {

    // Inserts budget settings into the table
    // If a record with the same primary key exists, it will be replaced
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: BudgetSettings)

    // Retrieves the budget settings with a fixed ID of 1
    // Assumes there will only be one settings record in the table
    @Query("SELECT * FROM budget_settings WHERE id = 1")
    suspend fun getSettings(): BudgetSettings?
}

