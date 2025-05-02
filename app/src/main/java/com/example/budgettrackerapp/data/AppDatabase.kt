
package com.example.budgettrackerapp.data


import com.example.budgettrackerapp.data.Expense
import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Specifies the Room database configuration with entities and version number
@Database(entities = [User::class, Expense::class, BudgetSettings::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    // Abstract functions to get DAO interfaces for interacting with the database tables
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetSettingsDao(): BudgetSettingsDao

    companion object {
        // The INSTANCE variable will hold a reference to the database once it’s created
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // This function returns the database instance, creating it if it doesn’t exist
        fun getDatabase(context: Context): AppDatabase {
            // Use synchronized to ensure thread safety when creating the database instance
            return INSTANCE ?: synchronized(this) {
                // Build the database with Room, using the application context to avoid leaks
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_db" // The name of the database file
                )
                    // Wipes and rebuilds instead of migrating if no migration is provided
                    .fallbackToDestructiveMigration()
                    .build()

                // Save the instance to the INSTANCE variable and return it
                INSTANCE = instance
                instance
            }
        }
    }
}

