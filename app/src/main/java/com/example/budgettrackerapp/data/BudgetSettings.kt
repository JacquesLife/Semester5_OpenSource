package com.example.budgettrackerapp.data

// Budget settings data class for Firebase Realtime Database
data class BudgetSettings(
    val id: String = "",               // Firebase-generated ID or user-specific ID
    val userId: String = "",           // User ID who owns these settings
    val monthlyBudget: Double = 0.0,   // Monthly budget amount
    val monthlyMaxGoal: Double = 0.0,  // Monthly maximum goal
    val monthlyMinGoal: Double = 0.0   // Monthly minimum goal
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", 0.0, 0.0, 0.0)
}
