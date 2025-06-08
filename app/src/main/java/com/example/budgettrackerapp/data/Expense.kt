package com.example.budgettrackerapp.data

// Expense data class for Firebase Realtime Database
data class Expense(
    val id: String = "",              // Firebase-generated ID
    val amount: Double = 0.0,         // Expense amount
    val date: String = "",            // Date of the expense (e.g., "2025-05-02")
    val dueDate: String = "",         // Due date for bills/recurring expenses (e.g., "2025-05-15")
    val isRecurring: Boolean = false, // Whether this is a recurring expense/bill
    val recurringInterval: String = "", // "monthly", "weekly", "yearly" for recurring expenses
    val startTime: String = "",       // Optional: start time (e.g., for time tracking or events)
    val endTime: String = "",         // Optional: end time
    val description: String = "",     // Description or notes about the expense
    val category: String = "",        // Expense category (e.g., "Food", "Transport")
    val photoUri: String? = null,     // Optional URI to a photo receipt or proof of expense
    val userOwnerId: String = "",     // User ID who owns this expense
    val notificationEnabled: Boolean = true, // Whether to send notifications for this expense
    val notificationDaysBefore: Int = 3 // How many days before due date to notify
) {
    // No-argument constructor required by Firebase
    constructor() : this("", 0.0, "", "", false, "", "", "", "", "", null, "", true, 3)
}
