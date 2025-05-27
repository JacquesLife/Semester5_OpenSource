package com.example.budgettrackerapp.data

// Expense data class for Firebase Realtime Database
data class Expense(
    val id: String = "",              // Firebase-generated ID
    val amount: Double = 0.0,         // Expense amount
    val date: String = "",            // Date of the expense (e.g., "2025-05-02")
    val startTime: String = "",       // Optional: start time (e.g., for time tracking or events)
    val endTime: String = "",         // Optional: end time
    val description: String = "",     // Description or notes about the expense
    val category: String = "",        // Expense category (e.g., "Food", "Transport")
    val photoUri: String? = null,     // Optional URI to a photo receipt or proof of expense
    val userOwnerId: String = ""      // User ID who owns this expense
) {
    // No-argument constructor required by Firebase
    constructor() : this("", 0.0, "", "", "", "", "", null, "")
}
