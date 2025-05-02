
package com.example.budgettrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_settings")
data class BudgetSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,  // NEW FIELD to relate settings to a user
    val monthlyBudget: Double,
    val monthlyMaxGoal: Double,
    val monthlyMinGoal: Double
)
