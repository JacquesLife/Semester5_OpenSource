
package com.example.budgettrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_settings")
data class BudgetSettings(
    @PrimaryKey val id: Int = 1,
    val monthlyBudget: Double,
    val monthlyMaxGoal: Double,
    val monthlyMinGoal: Double
)
