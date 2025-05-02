package com.example.budgettrackerapp.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.budgettrackerapp.data.User

// Declares the 'expenses' table with a foreign key relationship to the 'users' table
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = User::class,              // References the User entity
            parentColumns = ["userId"],        // Primary key in the parent table (users)
            childColumns = ["userOwnerId"],    // Foreign key in this table (expenses)
            onDelete = ForeignKey.CASCADE      // Deletes related expenses if user is deleted
        )
    ],
    indices = [Index(value = ["userOwnerId"])] // Improves query performance on foreign key
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // Auto-generated primary key for expense

    val amount: Double,          // Expense amount
    val date: String,            // Date of the expense (e.g., "2025-05-02")
    val startTime: String,       // Optional: start time (e.g., for time tracking or events)
    val endTime: String,         // Optional: end time
    val description: String,     // Description or notes about the expense
    val category: String,        // Expense category (e.g., "Food", "Transport")
    val photoUri: String? = null,// Optional URI to a photo receipt or proof of expense

    val userOwnerId: Int         // Foreign key linking this expense to a user
)
