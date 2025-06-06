package com.example.budgettrackerapp.data

// Budget settings data class for Firebase Realtime Database
data class BudgetSettings(
    val id: String = "",                    // Firebase-generated ID or userId
    val userId: String = "",                // User ID who owns these settings
    val monthlyBudget: Double = 0.0,        // Total monthly budget
    val monthlyMinGoal: Double = 0.0,       // Minimum spending goal for rewards
    val monthlyMaxGoal: Double = 0.0        // Maximum spending goal for rewards
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", 0.0, 0.0, 0.0)
}